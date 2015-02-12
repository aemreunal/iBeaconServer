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

public class BeaconHasRegionException extends IllegalStateException {
    public BeaconHasRegionException(Long beaconId, Long regionId) {
        super("The beacon with ID " + beaconId + " belongs to region with ID " + regionId + ". No modifications have been made.");
    }
}
