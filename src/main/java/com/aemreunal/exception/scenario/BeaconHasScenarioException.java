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

public class BeaconHasScenarioException extends IllegalStateException {

    public BeaconHasScenarioException(Long beaconId, Long scenarioId) {
        super("The beacon with ID " + beaconId + " is part of scenario with ID " + scenarioId + ". No modifications have been made.");
    }
}
