package com.comcast.ibis.kingfisherclient;

import com.comcast.ibis.kingfisher.*;

import com.google.protobuf.ByteString;
import org.apache.commons.lang3.StringUtils;

import java.lang.Error;
import java.util.Map;


/**
 * The type Device.
 */
public class Device {
    /**
     * The Device data.
     */
    public com.comcast.ibis.kingfisher.Device deviceData;
    /**
     * The Rack data.
     */
    public RackData rackData;
    /**
     * The Device metadata.
     */
    public DeviceMetadata deviceMetadata;
    /**
     * The Device ref.
     */
    public DeviceReference deviceRef;

    private KingfisherGrpc.KingfisherBlockingStub stub;

    private Map<String, String> secretManager;

    /**
     * Instantiates a new Device.
     *
     * @param deviceData     the device data
     * @param rackData       the rack data
     * @param deviceMetadata the device metadata
     * @param deviceRef      the device ref
     * @param stub           the stub
     * @param secretManager  the secret manager
     */
    public Device(com.comcast.ibis.kingfisher.Device deviceData, RackData rackData, DeviceMetadata deviceMetadata, DeviceReference deviceRef, KingfisherGrpc.KingfisherBlockingStub stub, Map<String, String> secretManager) {
        this.deviceData = deviceData;
        this.rackData = rackData;
        this.deviceMetadata = deviceMetadata;
        this.deviceRef = deviceRef;
        this.stub = stub;
        this.secretManager = secretManager;
    }

    /**
     * Lock string.
     *
     * @return the string
     */
    String lock() {
        LockDeviceResponse res;
        try {
            LockDeviceRequest req = LockDeviceRequest.newBuilder()
                    .setDeviceId(deviceRef.getDevice())
                    .setOrg(deviceRef.getOrg())
                    .build();
            res = this.stub.lockDevice(req);
        } catch (Exception e) {
            throw new Error("unable to lock device");
        }

        return res.getResult().getReservation().getReservationSecret();
    }


    /**
     * Lock string.
     *
     * @param secret the secret
     * @return the string
     */
    String lock(String secret) {
        LockDeviceResponse res;
        if ((StringUtils.stripToNull(secret) == null)) {
            throw new IllegalArgumentException("reservation secret is not present");
        }
        try {
            LockDeviceRequest req = LockDeviceRequest.newBuilder()
                    .setDeviceId(deviceRef.getDevice())
                    .setOrg(deviceRef.getOrg())
                    .setReservationSecret(secret)
                    .build();
            res = this.stub.lockDevice(req);
            return res.getResult().getReservation().getReservationSecret();
        } catch (Exception e) {
            throw new Error("unable to lock device");
        }
    }

    /**
     * Unlock.
     *
     * @param secret the secret
     */
    void unlock(String secret) {

        if (secret == null) {
            throw new IllegalArgumentException("reservation secret must be provided");
        }

        UnlockDeviceRequest req = UnlockDeviceRequest.newBuilder()
                .setDeviceId(deviceRef.getDevice())
                .setReservationSecret(secret)
                .setOrg(deviceRef.getOrg())
                .build();
        UnlockDeviceResponse res = this.stub.unlockDevice(req);
    }

    /**
     * Launch app.
     *
     * @param appConfig the app config
     */
    public void launchApp(AppConfig appConfig) {
        LaunchAppRequest req = LaunchAppRequest.newBuilder()
                .setDeviceId(deviceRef.getDevice())
                .setAppConfig(appConfig)
                .setReservationSecret(secretManager.get(deviceRef.getDevice()))
                .setOrg(deviceRef.getOrg())
                .build();
        LaunchAppResponse res = this.stub.launchApp(req);
    }

    /**
     * Launch app string.
     *
     * @param appConfig      the app config
     * @param deepLinkParams the deep link params
     * @return the string
     */
    public String launchApp(AppConfig appConfig,DeeplinkParams deepLinkParams) {
        LaunchAppRequest req = LaunchAppRequest.newBuilder()
                .setDeviceId(deviceRef.getDevice())
                .setAppConfig(appConfig)
                .setReservationSecret(secretManager.get(deviceRef.getDevice()))
                .setOrg(deviceRef.getOrg())
                .setNotStackable(deepLinkParams.isNotStackable())
                .setDisableVoiceOut((deepLinkParams.isDisableVoiceOut()))
                .setDryRun(deepLinkParams.isDryRun())
                .setVoiceGuidanceMode(deepLinkParams.getVoiceGuidanceMode())
                .putAllAdditionalParams(deepLinkParams.getAdditionalParams())
                .setEnableScreensaver(deepLinkParams.isEnableScreensaver())
                .build();
        LaunchAppResponse res = this.stub.launchApp(req);
        return res.getResult().getDeeplink();
    }

    /**
     * Deeplink string.
     *
     * @param deeplink the deeplink
     * @return the string
     */
    public String deeplink(String deeplink) {

        if (StringUtils.stripToNull(deeplink) == null) {
            throw new IllegalArgumentException("deeplink  must be provided");
        }

        DeeplinkRequest req = DeeplinkRequest.newBuilder()
                .setDeviceId(deviceRef.getDevice())
                .setReservationSecret(secretManager.get(deviceRef.getDevice()))
                .setDeeplink(deeplink)
                .setOrg(deviceRef.getOrg())
                .build();
        DeeplinkResponse res = this.stub.deeplink(req);
         return res.getResult().getDeeplink();
    }

    /**
     * Press key.
     *
     * @param key the key
     */
    public void pressKey(String key) {

        if (StringUtils.stripToNull(key) == null) {
            throw new IllegalArgumentException("key  must be provided");
        }

        PressKeyRequest req = PressKeyRequest.newBuilder()
                .setDeviceId(deviceRef.getDevice())
                .setReservationSecret(secretManager.get(deviceRef.getDevice()))
                .setKey(key)
                .setOrg(deviceRef.getOrg())
                .build();
        PressKeyResponse res = this.stub.pressKey(req);
    }


    /**
     * Screenshot byte string.
     *
     * @return the byte string
     */
    public ByteString screenshot() {

        ScreenshotRequest req = ScreenshotRequest.newBuilder()
                .setDeviceId(deviceRef.getDevice())
                .setOrg(deviceRef.getOrg())
                .build();
        ScreenshotResponse res = this.stub.screenshot(req);
        return res.getResult().getImage();

    }

    /**
     * Check alive check alive response.
     *
     * @return the check alive response
     */
    public CheckAliveResponse checkAlive() {

        CheckAliveRequest req = CheckAliveRequest.newBuilder().build().newBuilder()
                .setDeviceId(deviceRef.getDevice())
                .setOrg(deviceRef.getOrg())
                .build();
        return this.stub.checkAlive(req);
    }

    /**
     * Reboot reboot response.
     *
     * @return the reboot response
     */
    public RebootResponse reboot() {

        RebootRequest req = RebootRequest.newBuilder()
                .setDeviceId(deviceRef.getDevice())
                .setReservationSecret(secretManager.get(deviceRef.getDevice()))
                .setOrg(deviceRef.getOrg())
                .build();
        return this.stub.reboot(req);
    }

}
