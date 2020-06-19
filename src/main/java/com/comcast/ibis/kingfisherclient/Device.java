package com.comcast.ibis.kingfisherclient;

import com.comcast.ibis.kingfisher.*;
import com.comcast.ibis.kingfisherclient.common.Utils;

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

    private OwnerSpec ownerSpec;

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
        this.ownerSpec = Utils.getOwnerSpec(this.deviceMetadata);
    }

    /**
     * Lock string.
     *
     * @return the string
     */
    String lock() {
        LockDeviceResponse res;
        try {
            LockDeviceRequest.Builder req = LockDeviceRequest.newBuilder()
                    .setDeviceId(deviceRef.getDevice())
                    .setOrg(deviceRef.getOrg());
            if(ownerSpec != null) {
                req.setOwner(ownerSpec);
            }
            res = this.stub.lockDevice(req.build());
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
            LockDeviceRequest.Builder req = LockDeviceRequest.newBuilder()
                    .setDeviceId(deviceRef.getDevice())
                    .setOrg(deviceRef.getOrg())
                    .setReservationSecret(secret);
            if(ownerSpec != null) {
                req.setOwner(ownerSpec);
            }
            res = this.stub.lockDevice(req.build());
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

        UnlockDeviceRequest.Builder req = UnlockDeviceRequest.newBuilder()
                .setDeviceId(deviceRef.getDevice())
                .setReservationSecret(secret)
                .setOrg(deviceRef.getOrg());
        if(ownerSpec != null) {
            req.setOwner(ownerSpec);
        }
        UnlockDeviceResponse res = this.stub.unlockDevice(req.build());
    }

    /**
     * Launch app.
     *
     * @param appConfig the app config
     */
    public void launchApp(AppConfig appConfig) {
        LaunchAppRequest.Builder req = LaunchAppRequest.newBuilder()
                .setDeviceId(deviceRef.getDevice())
                .setAppConfig(appConfig)
                .setReservationSecret(secretManager.get(deviceRef.getDevice()))
                .setOrg(deviceRef.getOrg());
        if(ownerSpec != null) {
            req.setOwner(ownerSpec);
        }
        LaunchAppResponse res = this.stub.launchApp(req.build());
    }

    /**
     * Launch app string.
     *
     * @param appConfig      the app config
     * @param deepLinkParams the deep link params
     * @return the string
     */
    public String launchApp(AppConfig appConfig,DeeplinkParams deepLinkParams) {
        LaunchAppRequest.Builder req = LaunchAppRequest.newBuilder()
                .setDeviceId(deviceRef.getDevice())
                .setAppConfig(appConfig)
                .setReservationSecret(secretManager.get(deviceRef.getDevice()))
                .setOrg(deviceRef.getOrg())
                .setNotStackable(deepLinkParams.isNotStackable())
                .setDisableVoiceOut((deepLinkParams.isDisableVoiceOut()))
                .setDryRun(deepLinkParams.isDryRun())
                .setVoiceGuidanceMode(deepLinkParams.getVoiceGuidanceMode())
                .putAllAdditionalParams(deepLinkParams.getAdditionalParams())
                .setEnableScreensaver(deepLinkParams.isEnableScreensaver());
        if(ownerSpec != null) {
            req.setOwner(ownerSpec);
        }
        LaunchAppResponse res = this.stub.launchApp(req.build());
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

        DeeplinkRequest.Builder req = DeeplinkRequest.newBuilder()
                .setDeviceId(deviceRef.getDevice())
                .setReservationSecret(secretManager.get(deviceRef.getDevice()))
                .setDeeplink(deeplink)
                .setOrg(deviceRef.getOrg());
        if(ownerSpec != null) {
            req.setOwner(ownerSpec);
        }
        DeeplinkResponse res = this.stub.deeplink(req.build());
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

        PressKeyRequest.Builder req = PressKeyRequest.newBuilder()
                .setDeviceId(deviceRef.getDevice())
                .setReservationSecret(secretManager.get(deviceRef.getDevice()))
                .setKey(key)
                .setOrg(deviceRef.getOrg());
        if(ownerSpec != null) {
            req.setOwner(ownerSpec);
        }
        PressKeyResponse res = this.stub.pressKey(req.build());
    }


    /**
     * Screenshot byte string.
     *
     * @return the byte string
     */
    public ByteString screenshot() {

        ScreenshotRequest.Builder req = ScreenshotRequest.newBuilder()
                .setDeviceId(deviceRef.getDevice())
                .setOrg(deviceRef.getOrg());
        if(ownerSpec != null) {
            req.setOwner(ownerSpec);
        }
        ScreenshotResponse res = this.stub.screenshot(req.build());
        return res.getResult().getImage();

    }

    /**
     * Check alive check alive response.
     *
     * @return the check alive response
     */
    public CheckAliveResponse checkAlive() {
        CheckAliveRequest.Builder req = CheckAliveRequest.newBuilder()
                .setDeviceId(deviceRef.getDevice())
                .setOrg(deviceRef.getOrg());
        if(ownerSpec != null) {
             req.setOwner(ownerSpec);
        }
        return this.stub.checkAlive(req.build());
    }

    /**
     * Reboot reboot response.
     *
     * @param rebootType the reboot type
     * @return the reboot response
     */
    public RebootResponse reboot(RebootType rebootType) {

        RebootRequest.Builder req = RebootRequest.newBuilder()
                .setDeviceId(deviceRef.getDevice())
                .setReservationSecret(secretManager.get(deviceRef.getDevice()))
                .setRebootType(rebootType)
                .setOrg(deviceRef.getOrg());
        if(ownerSpec != null) {
            req.setOwner(ownerSpec);
        }
        return this.stub.reboot(req.build());
    }

    /**
     * Gets redirector type.
     *
     * @return the redirector type
     */
    public GetRedirectorResponse getRedirectorType() {
        GetRedirectorRequest.Builder req = GetRedirectorRequest.newBuilder()
                .setOrg(deviceRef.getOrg())
                .setDeviceId(deviceRef.getDevice());
        if(ownerSpec != null) {
            req.setOwner(ownerSpec);
        }
        return this.stub.getRedirectorType(req.build());
    }

    /**
     * Redirect redirect link response.
     *
     * @param link the link
     * @param list the list
     * @return the redirect link response
     */
    public RedirectLinkResponse redirect(String link, RedirectorType list) {
        RedirectLinkRequest.Builder req = RedirectLinkRequest.newBuilder()
                .setOrg(deviceRef.getOrg())
                .setDeviceId(deviceRef.getDevice())
                .setLink(link)
                .setList(list.toString());
        if(ownerSpec != null) {
            req.setOwner(ownerSpec);
        }
        return this.stub.redirect(req.build());
    }

    /**
     * Simulate voice input simulate voice input response.
     *
     * @param phrase   the phrase
     * @param language the language
     * @return the simulate voice input response
     */
    public SimulateVoiceInputResponse simulateVoiceInput(String phrase, String language) {
        SimulateVoiceInputRequest.Builder req = SimulateVoiceInputRequest.newBuilder()
                .setOrg(deviceRef.getOrg())
                .setDeviceId(deviceRef.getDevice())
                .setPhrase(phrase)
                .setLanguage(language)
                .setReservationSecret(secretManager.get(deviceRef.getDevice()));
        if(ownerSpec != null) {
            req.setOwner(ownerSpec);
        }
        return this.stub.simulateVoiceInput(req.build());
    }
}
