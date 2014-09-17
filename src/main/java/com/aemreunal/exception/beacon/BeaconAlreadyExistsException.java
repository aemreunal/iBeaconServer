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

import com.aemreunal.domain.Beacon;

public class BeaconAlreadyExistsException extends IllegalArgumentException {

    public BeaconAlreadyExistsException(Beacon beacon) {
        super("Beacon with UUID:\"" + beacon.getUuid() + "\", Major:\"" + beacon.getMajor() + "\", Minor:\"" + beacon.getMinor() + "\" already exists!");
    }
}
