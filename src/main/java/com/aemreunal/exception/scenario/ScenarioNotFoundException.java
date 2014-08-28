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
 * aemreunal@gmail.com     *
 * emre.unal@ozu.edu.tr    *
 *                         *
 * aemreunal.com           *
 ***************************
 */

public class ScenarioNotFoundException  extends NullPointerException {
    public ScenarioNotFoundException() {
        super("The requested Scenario can not be found!");
    }

    public ScenarioNotFoundException(Long scenarioId) {
        super("The requested Scenario with ID " + scenarioId + " can not be found!");
    }

}
