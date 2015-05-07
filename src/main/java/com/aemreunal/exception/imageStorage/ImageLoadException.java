package com.aemreunal.exception.imageStorage;

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

public class ImageLoadException extends Exception {
    public ImageLoadException(Long projectId, Long regionId) {
        super(getExceptionMessage(projectId, regionId));
    }

    private static String getExceptionMessage(Long projectId, Long regionId) {
        if (regionId == null) {
            return "Unable to load the image file of project " + projectId + "!";
        } else {
            return "Unable to load the image file of project " + projectId + ", region " + regionId + "!";
        }
    }
}
