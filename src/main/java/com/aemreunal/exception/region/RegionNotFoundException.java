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

public class RegionNotFoundException extends NullPointerException {
    public RegionNotFoundException() {
        super("The requested region can not be found!");
    }

    public RegionNotFoundException(Long regionId) {
        super("The requested region with ID " + regionId + " can not be found!");
    }
}
