package com.aemreunal.exception.beaconGroup;

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

public class BeaconHasGroupException extends IllegalStateException {
    public BeaconHasGroupException(Long beaconId, Long beaconGroupId) {
        super("The beacon with ID " + beaconId + " belongs to group with ID " + beaconGroupId + ". No modifications have been made.");
    }
}
