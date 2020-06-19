package com.comcast.ibis.kingfisherclient;

import com.comcast.ibis.kingfisher.KingfisherGrpc;
import com.comcast.ibis.kingfisher.DeviceData;
import com.comcast.ibis.kingfisher.GetDevicesRequest;
import com.comcast.ibis.kingfisher.GetDevicesResponse;
import com.comcast.ibis.kingfisherclient.common.Utils;

import io.grpc.ManagedChannelBuilder;
import io.grpc.ClientCall;
import io.grpc.ManagedChannel;
import io.grpc.ClientInterceptor;
import io.grpc.CallCredentials;
import io.grpc.MethodDescriptor;
import io.grpc.Metadata;
import io.grpc.CallOptions;
import io.grpc.Channel;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * The type Kingfisher client.
 */
public class KingfisherClient {
    private static final int REFRESH_THRESHOLD_SECONDS = 30;
    private static final String AUTH_METADATA = "authorization";

    private String token;
    private KingfisherClientBuilder.TokenType tokenType;
    private String org;
    private String host;
    /**
     * The Released.
     */
    AtomicBoolean released = new AtomicBoolean(false);

    private ManagedChannel channel;
    private final AuthorizationService auth;
    private KingfisherGrpc.KingfisherBlockingStub stub;
    private Lock lock = new ReentrantLock();
    private Map<String, String> secretManager;
    private Set<Device> refreshDevices;
    private Set<Device> syncRefreshDevices;
    private Thread thread;


    private KingfisherClient(KingfisherClientBuilder builder) {
        this.token = builder.token;
        this.tokenType = builder.tokenType;
        this.host = builder.host;
        this.auth = new AuthorizationService(token, host);
        this.secretManager = new HashMap<>();
        this.refreshDevices = new HashSet();
        this.syncRefreshDevices = Collections.synchronizedSet(refreshDevices);
    }

    /**
     * Authorize current user.
     *
     * @throws IOException the io exception
     */
    void authorizeCurrentUser() throws IOException {
        this.org = auth.getCurrentUser().getOrg();
    }

    /**
     * Start.
     *
     * @throws IOException the io exception
     */
    public void start() throws IOException {
        authorizeCurrentUser();
        if(this.host.contains("localhost")) {
            this.channel = ManagedChannelBuilder.forTarget(this.host)
                    .usePlaintext()
                    .build();
        } else {
            this.channel = ManagedChannelBuilder.forTarget(this.host)
                    .useTransportSecurity()
                    .build();
        }

        this.stub = KingfisherGrpc.newBlockingStub(channel).withInterceptors(new ClientInterceptor() {
            @Override
            public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> methodDescriptor, CallOptions callOptions, Channel channel) {
                callOptions = callOptions.withCallCredentials(new CallCredentials() {
                    @Override
                    public void applyRequestMetadata(RequestInfo requestInfo, Executor executor, MetadataApplier metadataApplier) {
                        Metadata.Key<String> apiKeys = Metadata.Key.of(AUTH_METADATA, Metadata.ASCII_STRING_MARSHALLER);
                        Metadata header = new Metadata();
                        if(tokenType.equals(KingfisherClientBuilder.TokenType.BEARER)) {
                            header.put(apiKeys, "bearer " + token);
                        } else {
                            header.put(apiKeys, "apikey " + token);
                        }
                        metadataApplier.apply(header);
                    }

                    @Override
                    public void thisUsesUnstableApi() {
                    }
                });
                return channel.newCall(methodDescriptor, callOptions);
            }
        });
        refreshDevices();
    }

    /**
     * Gets devices.
     *
     * @return the devices
     */
    List<DeviceData> getDevices() {

        GetDevicesRequest req = GetDevicesRequest.newBuilder()
                .setOrg(org)
                .setIncludeRackData(true)
                .setVerbose(true)
                .build();
        GetDevicesResponse res = stub.getDevices(req);
        List<DeviceData> result = res.getResultList();
        return result;
    }

    /**
     * Private devices list.
     *
     * @return the list
     * @throws IOException the io exception
     */
    public List<DeviceData> privateDevices() throws IOException {
        String currentUser = auth.getCurrentUser().getId();
        List<DeviceData> result = new ArrayList();

        getDevices().forEach(d -> {
            if(d.getMetadata().getOwner().getUser().getUser().equals(currentUser)) {
                result.add(d);
            }
        });
        return result;
    }

    /**
     * Search devices list.
     *
     * @param predicate the predicate
     * @return the list
     */
    public List<Device> searchDevices(Predicate<DeviceData> predicate) {
        return Utils.deviceList(this.getDevices().stream().filter(predicate).collect(Collectors.toList()), stub, secretManager);
    }

    /**
     * Reserve.
     *
     * @param device the device
     */
    public void reserve(Device device) {
        try {
            this.lock.lock();
            String secret = (secretManager.containsKey(device.deviceRef.getDevice())) ?  device.lock(secretManager.get(device.deviceRef.getDevice())) : device.lock();
            secretManager.put(device.deviceRef.getDevice(), secret);
            syncRefreshDevices.add(device);

        } catch (Exception e) {
            throw new Error("unable to reserve devices");
        }
        finally {
            this.lock.unlock();
        }
    }

    /**
     * Reserve.
     *
     * @param devices the devices
     */
    public void reserve(List<Device> devices) {
        devices.forEach((device) -> {
            try {
                reserve(device);
            } catch (Exception e) {
                throw new Error("unable to reserve devices");
            }

        });
    }

    private void refreshDevices() {
        Runnable runnable = () -> {
            while (!released.get()) {
                if(!syncRefreshDevices.isEmpty()) {
                    syncRefreshDevices.forEach((device) -> {
                        try {
                            reserve(device);
                        } catch (Exception e) {
                            throw new Error("unable to refresh devices");
                        }

                    });
                }

                try {
                    Thread.sleep(TimeUnit.SECONDS.toMillis(REFRESH_THRESHOLD_SECONDS));
                } catch (InterruptedException e) { }

            }
        };
        thread = new Thread(runnable);
        thread.start();
    }

    /**
     * Release.
     *
     * @param device the device
     */
    public void release(Device device) {
        try {
            this.lock.lock();
            if(device.checkAlive().getResult().getLocked()) {
                device.unlock(secretManager.get(device.deviceRef.getDevice()));
                secretManager.remove(device.deviceRef.getDevice());
                syncRefreshDevices.remove(device);
            }
        } catch (Exception e) {

        }
        finally {
            this.lock.unlock();
        }
    }

    /**
     * Release.
     *
     * @param devices the devices
     */
    public void release(List<Device> devices) {
        devices.forEach((device) -> {
            try {
                release(device);
            } catch (Exception e) { }
        });
    }


    /**
     * Stop.
     *
     * @throws InterruptedException the interrupted exception
     */
    public void stop() throws InterruptedException {
        this.released.getAndSet(true);
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        try {
            thread.interrupt();
            thread.join();
        } catch (InterruptedException e) { }
    }

    /**
     * New builder kingfisher client builder.
     *
     * @return the kingfisher client builder
     */
    public static KingfisherClientBuilder newBuilder() {
        return new KingfisherClientBuilder();
    }

    /**
     * The type Kingfisher client builder.
     */
    public static class KingfisherClientBuilder {
        private String host;
        private String token;

        private  TokenType tokenType;

        /**
         * The enum Token type.
         */
        public enum TokenType
        {
            /**
             * Bearer token type.
             */
            BEARER,
            /**
             * Apikey token type.
             */
            APIKEY;
        }

        /**
         * Instantiates a new Kingfisher client builder.
         */
        public KingfisherClientBuilder() { }

        /**
         * Sets host.
         *
         * @param host the host
         * @return the host
         */
        public KingfisherClientBuilder setHost(String host) {
            this.host = host;
            return this;
        }


        /**
         * Sets token.
         *
         * @param token     the token
         * @param tokenType the token type
         * @return the token
         */
        public KingfisherClientBuilder setToken(String token, TokenType tokenType) {
            this.token = token;
            this.tokenType = tokenType;
            return this;
        }


        /**
         * Build kingfisher client.
         *
         * @return the kingfisher client
         */
        public KingfisherClient build() {
            return new KingfisherClient(this);
        }
    }
}
