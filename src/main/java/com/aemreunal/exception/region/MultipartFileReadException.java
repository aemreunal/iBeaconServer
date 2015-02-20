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

public class MultipartFileReadException extends Exception {
    public MultipartFileReadException(Long projectId, Long regionId) {
        super("Unable to read the Multipart file, uploaded as the map image of project " + projectId + ", region " + regionId + "!");
    }
}
