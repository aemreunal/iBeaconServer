package com.aemreunal.exception.region;

/*
 ***************************
 * Copyright (c) 2015      *
 *                         *
 * This code belongs to:   *
 *                         *
 * @author Ahmet Emre Ünal *
 * S001974                 *
 *                         *
 * emre@aemreunal.com      *
 * emre.unal@ozu.edu.tr    *
 *                         *
 * aemreunal.com           *
 ***************************
 */

public class MapImageNotSetException extends Exception {
    public MapImageNotSetException(Long projectId, Long regionId) {
        super("Map image of region " + regionId + " in project " + projectId + " has not been set!");
    }
}
