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

public class BeaconWithGroupScenarioException extends IllegalStateException {

    public BeaconWithGroupScenarioException(Long beaconId, Long beaconGroupId) {
        super("The beacon with ID " + beaconId + " belongs to group with ID " + beaconGroupId + ". Any changes to scenario must be made on the group. No modifications have been made.");
    }
}
