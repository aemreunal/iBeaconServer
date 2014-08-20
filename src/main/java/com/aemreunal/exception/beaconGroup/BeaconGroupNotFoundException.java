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

public class BeaconGroupNotFoundException extends NullPointerException  {
    public BeaconGroupNotFoundException() {
        super("The requested beacon group can not be found!");
    }

    public BeaconGroupNotFoundException(Long beaconGroupId) {
        super("The requested beacon group with ID " + beaconGroupId + " can not be found!");
    }
}
