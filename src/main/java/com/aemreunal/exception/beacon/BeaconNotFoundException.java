package com.aemreunal.exception.beacon;

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

public class BeaconNotFoundException extends NullPointerException {
    public BeaconNotFoundException() {
        super("The requested Beacon can not be found!");
    }

    public BeaconNotFoundException(Long beaconId) {
        super("The requested Beacon with ID " + beaconId + " can not be found!");
    }
}
