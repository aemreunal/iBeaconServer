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

import net.minidev.json.JSONObject;

import java.util.UUID;
import com.aemreunal.domain.EntityCreator;
import com.aemreunal.helper.JsonBuilder;
import com.jayway.restassured.path.json.JsonPath;

import static org.junit.Assert.assertEquals;

public class BeaconCreator extends EntityCreator {
    public static BeaconInfo createRandomBeacon(String ownerUsername, Long projectId) {
         return createBeacon(ownerUsername, projectId, "", "", "", "");
    }

    private static BeaconInfo createBeacon(String ownerUsername, Long projectId, String uuid, String major, String minor, String description) {
        uuid = checkUUID(uuid);
        major = checkMajor(major);
        minor = checkMinor(minor);
        description = checkDescription(description);

        JSONObject beaconJson = getBeaconCreateJson(uuid, major, minor, description);
        String path = getBeaconCreatePath(ownerUsername, projectId);
        JsonPath responseJson = createEntity(beaconJson, path);

        assertEquals("Requested beacon uuid and response beacon uuid do not match!", uuid, responseJson.getString("uuid"));
        assertEquals("Requested beacon major and response beacon major do not match!", major, responseJson.getString("major"));
        assertEquals("Requested beacon minor and response beacon minor do not match!", minor, responseJson.getString("minor"));
        assertEquals("Requested beacon description and response beacon description do not match!", description, responseJson.getString("description"));

        return new BeaconInfo(responseJson, projectId, ownerUsername);
    }

    private static String getBeaconCreatePath(String ownerUsername, Long projectId) {
        return "/" + ownerUsername + "/projects/" + projectId + "/beacons";
    }

    private static JSONObject getBeaconCreateJson(String uuid, String major, String minor, String description) {
        return new JsonBuilder().add("uuid", uuid)
                                .add("major", major)
                                .add("minor", minor)
                                .add("description", description)
                                .build();
    }

    private static String checkUUID(String uuid) {
        if(uuid.equals("")) {
            uuid = UUID.randomUUID().toString();
        }
        return uuid;
    }

    private static String checkMajor(String major) {
        if(major.equals("")) {
            major = String.valueOf(random.nextInt(10000));
        }
        return major;
    }

    private static String checkMinor(String minor) {
        if(minor.equals("")) {
            minor = String.valueOf(random.nextInt(10000));
        }
        return minor;
    }
}
