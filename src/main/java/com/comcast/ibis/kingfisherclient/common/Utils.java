package com.comcast.ibis.kingfisherclient.common;

import com.comcast.ibis.kingfisher.DeviceMetadata;
import com.comcast.ibis.kingfisher.OwnerSpec;
import com.comcast.ibis.kingfisherclient.Device;
import com.comcast.ibis.kingfisher.DeviceData;
import com.comcast.ibis.kingfisher.KingfisherGrpc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * The type Utils.
 */
public class Utils {
    /**
     * Device list list.
     *
     * @param deviceData    the device data
     * @param stub          the stub
     * @param secretManager the secret manager
     * @return the list
     */
    public static List<Device> deviceList(List<DeviceData> deviceData, KingfisherGrpc.KingfisherBlockingStub stub, Map<String, String> secretManager) {
        List<Device> deviceList = new ArrayList<>();
        deviceData.forEach((item) -> {
            Device newDevice = new Device(
                    item.getDevice(), item.getRackdata(), item.getMetadata(), item.getReference(), stub, secretManager);
            deviceList.add(newDevice);
        });
        return deviceList;
    }

    /**
     * Gets owner spec.
     *
     * @param deviceMetadata the device metadata
     * @return the owner spec
     */
    public static OwnerSpec getOwnerSpec(DeviceMetadata deviceMetadata) {
        String userId = deviceMetadata.getOwner().getUser().getUser();
        String groupId = deviceMetadata.getOwner().getGroup().getName();
        OwnerSpec ownerSpec = (!userId.isEmpty()) ? OwnerSpec.newBuilder().setUser(userId).build() : (!groupId.isEmpty() ? OwnerSpec.newBuilder().setGroup(groupId).build() : null);
        return ownerSpec;
    }
}
