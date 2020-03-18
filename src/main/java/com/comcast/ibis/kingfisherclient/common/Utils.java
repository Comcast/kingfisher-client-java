package com.comcast.ibis.kingfisherclient.common;

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
     * @param deviceData the device data
     * @param stub       the stub
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
}
