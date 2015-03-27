package com.aemreunal.controller.scenario;

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

import net.minidev.json.JSONObject;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import com.aemreunal.exception.scenario.BeaconDoesNotHaveScenarioException;
import com.aemreunal.exception.scenario.BeaconHasScenarioException;
import com.aemreunal.exception.scenario.ScenarioNotFoundException;
import com.aemreunal.helper.JsonBuilder;

@ControllerAdvice
public class ScenarioControllerAdvice {
    @ExceptionHandler(ScenarioNotFoundException.class)
    public ResponseEntity<JSONObject> scenarioNotFoundExceptionHandler(ScenarioNotFoundException ex) {
        JSONObject responseBody = new JsonBuilder(JsonBuilder.OBJECT).addToObj("reason", "scenario")
                                                   .addToObj("error", ex.getLocalizedMessage())
                                                   .buildObj();
        return new ResponseEntity<JSONObject>(responseBody, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BeaconHasScenarioException.class)
    public ResponseEntity<JSONObject> beaconHasScenarioExceptionHandler(BeaconHasScenarioException ex) {
        JSONObject responseBody = new JsonBuilder(JsonBuilder.OBJECT).addToObj("reason", "scenario")
                                                   .addToObj("error", ex.getLocalizedMessage())
                                                   .buildObj();
        return new ResponseEntity<JSONObject>(responseBody, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(BeaconDoesNotHaveScenarioException.class)
    public ResponseEntity<JSONObject> beaconDoesNotHaveScenarioExceptionHandler(BeaconDoesNotHaveScenarioException ex) {
        JSONObject responseBody = new JsonBuilder(JsonBuilder.OBJECT).addToObj("reason", "scenario")
                                                   .addToObj("error", ex.getLocalizedMessage())
                                                   .buildObj();
        return new ResponseEntity<JSONObject>(responseBody, HttpStatus.BAD_REQUEST);
    }
}
