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

public class WrongFileTypeSubmittedException extends Exception {
    public WrongFileTypeSubmittedException(Long projectId, Long regionId) {
        super("The submitted file, uploaded as the map image of project " + projectId + ", region " + regionId + ", is of a wrong type! Please submit an image file.");
    }
}
