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

public class TextSaveException extends Exception {
    public TextSaveException(Long projectId, Long regionId, Long beaconId) {
        super("Unable to save text file for project: " + projectId + ", region: " + regionId + ", beacon: " + beaconId);
    }
}
