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

public class TextDeleteException extends Exception {
    public TextDeleteException(Long projectId, Long regionId, Long beaconId) {
        super("Unable to delete the text file of project: " + projectId + ", region: " + regionId + ", beacon: " + beaconId + "!");
    }
}
