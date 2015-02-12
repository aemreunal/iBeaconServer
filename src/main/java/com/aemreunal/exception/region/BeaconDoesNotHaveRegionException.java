package com.aemreunal.exception.region;

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

public class BeaconDoesNotHaveRegionException extends IllegalStateException {
    public BeaconDoesNotHaveRegionException(Long beaconId, Long regionId) {
        super("The beacon with ID " + beaconId + " doesn't belong to the region with ID " + regionId + ". No modifications have been made.");
    }
}
