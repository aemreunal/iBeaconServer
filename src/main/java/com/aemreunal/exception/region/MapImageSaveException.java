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

public class MapImageSaveException extends Throwable {
    public MapImageSaveException(Long projectId, Long regionId) {
        super(createCauseMessage(projectId, regionId), null, true, false);
    }

    public static String createCauseMessage(Long projectId, Long regionId) {
        String message = "Unable to save ";
        if (regionId != null) {
            return message + "map image of project " + projectId + ", region " + regionId + "!";
        } else {
            return message + "a navigation image of project " + projectId + "!";
        }
    }
}
