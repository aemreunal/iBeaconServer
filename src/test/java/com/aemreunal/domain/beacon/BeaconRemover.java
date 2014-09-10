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

import org.apache.http.HttpStatus;
import com.aemreunal.domain.EntityRemover;

public class BeaconRemover extends EntityRemover {
    public static void removeBeacon(String username, Long projectId, Long beaconId) {
        removeEntity("/" + username + "/projects/" + projectId + "/beacons/" + beaconId);
    }

    public static void failToRemoveBeacon(String username, Long projectId, Long beaconId) {
        sendDeleteRequest("/" + username + "/projects/" + projectId + "/beacons/" + beaconId + "?confirm=yes", HttpStatus.SC_NOT_FOUND);
    }
}
