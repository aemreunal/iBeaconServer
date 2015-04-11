package com.aemreunal.exception.textStorage;

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

public class TextLoadException extends Exception {
    public TextLoadException(Long projectId, Long regionId, Long beaconId) {
        super("Unable to load the text file of project: " + projectId + ", region: " + regionId + ", beacon: " + beaconId + "!");
    }
}
