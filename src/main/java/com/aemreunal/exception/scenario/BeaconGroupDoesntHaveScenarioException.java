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

public class BeaconGroupDoesntHaveScenarioException extends IllegalStateException {

    public BeaconGroupDoesntHaveScenarioException(Long beaconGroupId, Long scenarioId) {
        super("The beacon group with ID " + beaconGroupId + " is not part of scenario with ID " + scenarioId + ". No modifications have been made.");
    }
}
