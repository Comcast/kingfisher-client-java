package com.comcast.ibis.kingfisherclient;

import com.comcast.ibis.kingfisher.DeviceData;
import java.util.function.Predicate;

/**
 * The type Search.
 */
public class Search {
    /**
     * Make predicate.
     *
     * @param make the make
     * @return the predicate
     */
    public static Predicate<DeviceData> make(String make) { return s-> s.getRackdata().getDeviceMake().equals(make); }

    /**
     * Model predicate.
     *
     * @param model the model
     * @return the predicate
     */
    public static Predicate<DeviceData> model(String model) {
        return s-> s.getDevice().getDeviceModel().equals(model);
    }

    /**
     * Name predicate.
     *
     * @param name the name
     * @return the predicate
     */
    public static Predicate<DeviceData> name(String name) {
        return s-> s.getMetadata().getName().equals(name);
    }

    /**
     * Device id predicate.
     *
     * @param deviceId the device id
     * @return the predicate
     */
    public static Predicate<DeviceData> deviceID(String deviceId) {
        return s-> s.getReference().getDevice().equals(deviceId);
    }

    /**
     * And predicate.
     *
     * @param funcs the funcs
     * @return the predicate
     */
    public static Predicate<DeviceData> and(Predicate<DeviceData>... funcs) {
        return dev -> {
          for (Predicate<DeviceData> pred: funcs) {
              if (!pred.test(dev)) {
                  return false;
              }
          }

          return true;
        };
    }

    /**
     * Or predicate.
     *
     * @param funcs the funcs
     * @return the predicate
     */
    public static Predicate<DeviceData> or(Predicate<DeviceData>... funcs) {
        return dev -> {
            for(Predicate<DeviceData> pred: funcs) {
                if (pred.test(dev)) {
                    return true;
                }
            }

            return false;
        };
    }
}
