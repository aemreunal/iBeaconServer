package com.aemreunal.exception.scenario;

/*
 ***************************
 * Copyright (c) 2014      *
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

public class BeaconWithRegionScenarioException extends IllegalStateException {
    public BeaconWithRegionScenarioException(Long beaconId, Long regionId) {
        super("The beacon with ID " + beaconId + " belongs to region with ID " + regionId + ". Any changes to scenario must be made on the region. No modifications have been made.");
    }
}
