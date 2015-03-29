package com.aemreunal.exception.connection;

/*
 * *********************** *
 * Copyright (c) 2015      *
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
 * *********************** *
 */

public class BeaconIsNotDesignatedException extends Exception {
    public BeaconIsNotDesignatedException(Long beaconId) {
        super("Unable to create connection, Beacon " + beaconId + " is not a designated beacon!");
    }
}
