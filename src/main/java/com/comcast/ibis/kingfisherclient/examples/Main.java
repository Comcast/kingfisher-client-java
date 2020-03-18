package com.comcast.ibis.kingfisherclient.examples;

import com.comcast.ibis.kingfisherclient.Device;
import com.comcast.ibis.kingfisherclient.KingfisherClient;
import com.comcast.ibis.kingfisherclient.Search;
import com.comcast.ibis.kingfisherclient.common.Constants;

import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        KingfisherClient kf = new KingfisherClient.KingfisherClientBuilder().setToken(System.getenv("apikey"), KingfisherClient.KingfisherClientBuilder.TokenType.APIKEY).setHost(Constants.KINGFISHER_SERVICE).build();
        kf.start();
        Thread threadOne = new Thread() {
            public void run() {
                List<Device> devices = kf.searchDevices(Search.or(Search.deviceID("<device id>")));
                kf.reserve(devices);
                devices.forEach(d-> {
                    d.deeplink("xre:///core/scenes/add?sceneName=chariot&type=presentation&url=%7BSTATIC_FILES_BASE_URL%7D%2Fcom%2Fcomcast%2Fcvs%2Fchariot%2Fpresentations%2Fchariot.xml%3FaccountId%3D%7BAPP%3AaccountId%7D%26appName%3Dtest%26controllerType%3Dhtml%26cookieMode%3DDEVICE%26debugMode%3Dtrue%26deviceId%3D%7BAPP%3AdeviceId%7D%26mutePlayer%3Dtrue%26platformEventTopic%3Dibis%26sessionId%3D%7BAPP%3AsessionId%7D%26timeZone%3D%7BAPP%3AtimeZone%7D%26url%3Dhttps%253A%252F%252Fgoogle.com");
                });
                kf.release(devices);
            }
        };

        // This is the second block of code
        Thread threadTwo = new Thread() {
            public void run() {
                List<Device> devices = kf.searchDevices(Search.or(Search.deviceID("<device id>")));
                kf.reserve(devices);
                devices.forEach(d-> {
                    d.deeplink("xre:///core/scenes/add?sceneName=chariot&type=presentation&url=%7BSTATIC_FILES_BASE_URL%7D%2Fcom%2Fcomcast%2Fcvs%2Fchariot%2Fpresentations%2Fchariot.xml%3FaccountId%3D%7BAPP%3AaccountId%7D%26appName%3Dtest%26controllerType%3Dhtml%26cookieMode%3DDEVICE%26debugMode%3Dtrue%26deviceId%3D%7BAPP%3AdeviceId%7D%26mutePlayer%3Dtrue%26platformEventTopic%3Dibis%26sessionId%3D%7BAPP%3AsessionId%7D%26timeZone%3D%7BAPP%3AtimeZone%7D%26url%3Dhttps%253A%252F%252Fgoogle.com");
                });
                kf.release(devices);
            }
        };

        threadOne.start();
        threadTwo.start();
        threadOne.join();
        threadTwo.join();
        kf.stop();
    }

}
