package com.aemreunal.controller.api;

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
import com.aemreunal.exception.scenario.NoScenarioForQueryException;
import com.aemreunal.helper.json.JsonBuilderFactory;

@ControllerAdvice
public class APIControllerAdvice {
    @ExceptionHandler(NoScenarioForQueryException.class)
    public ResponseEntity<JSONObject> beaconDoesntHaveScenarioExceptionHandler(NoScenarioForQueryException ex) {
        JSONObject responseBody = JsonBuilderFactory.object().add("reason", "scenario")
                                                   .add("error", ex.getLocalizedMessage())
                                                   .build();
        return new ResponseEntity<JSONObject>(responseBody, HttpStatus.BAD_REQUEST);
    }
}
