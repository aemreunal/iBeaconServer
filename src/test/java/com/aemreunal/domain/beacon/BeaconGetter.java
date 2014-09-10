package com.aemreunal.domain.beacon;

/*
 ***************************
 * Copyright (c) 2014      *
 *                         *
 * This code belongs to:   *
 *                         *
 * @author Ahmet Emre Ünal *
 * S001974                 *
 *                         *
 * aemreunal@gmail.com     *
 * emre.unal@ozu.edu.tr    *
 *                         *
 * aemreunal.com           *
 ***************************
 */

import java.util.ArrayList;
import java.util.HashMap;
import org.apache.http.HttpStatus;
import com.aemreunal.domain.EntityGetter;
import com.jayway.restassured.path.json.JsonPath;

public class BeaconGetter extends EntityGetter {
    public static ArrayList<BeaconInfo> getAllBeacons(String username, Long projectId) {
        JsonPath responseJson = getEntity("/" + username + "/projects/" + projectId + "/beacons");
        ArrayList<BeaconInfo> beacons = new ArrayList<>();
        for (HashMap beaconMap : responseJson.getList("", HashMap.class)) {
            beacons.add(new BeaconInfo(beaconMap, projectId, username));
        }
        return beacons;
    }

    public static void failToGetAllBeacons(String username, Long projectId) {
        sendGetRequest("/" + username + "/projects/" + projectId + "/beacons", HttpStatus.SC_NOT_FOUND);
    }

    public static BeaconInfo getBeacon(String username, Long projectId, Long beaconId) {
        JsonPath responseJson = getEntity("/" + username + "/projects/" + projectId + "/beacons/" + beaconId);
        return new BeaconInfo(responseJson, projectId, username);
    }

    public static void failToGetBeacon(String username, Long projectId, Long beaconId) {
        sendGetRequest("/" + username + "/projects/" + projectId + "/beacons/" + beaconId, HttpStatus.SC_NOT_FOUND);
    }
}
