package com.aemreunal.controller.scenario;

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

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import com.aemreunal.exception.scenario.*;
import com.aemreunal.helper.JsonBuilder;

@ControllerAdvice
public class ScenarioControllerAdvice {
    @ExceptionHandler(ScenarioNotFoundException.class)
    public ResponseEntity<JSONObject> scenarioNotFoundExceptionHandler(ScenarioNotFoundException ex) {
        JSONObject responseBody = new JsonBuilder().add("reason", "scenario")
                                                   .add("error", ex.getLocalizedMessage())
                                                   .build();
        return new ResponseEntity<JSONObject>(responseBody, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BeaconHasScenarioException.class)
    public ResponseEntity<JSONObject> beaconHasScenarioExceptionHandler(BeaconHasScenarioException ex) {
        JSONObject responseBody = new JsonBuilder().add("reason", "scenario")
                                                   .add("error", ex.getLocalizedMessage())
                                                   .build();
        return new ResponseEntity<JSONObject>(responseBody, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(BeaconDoesntHaveScenarioException.class)
    public ResponseEntity<JSONObject> beaconDoesntHaveScenarioExceptionHandler(BeaconDoesntHaveScenarioException ex) {
        JSONObject responseBody = new JsonBuilder().add("reason", "scenario")
                                                   .add("error", ex.getLocalizedMessage())
                                                   .build();
        return new ResponseEntity<JSONObject>(responseBody, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BeaconGroupHasScenarioException.class)
    public ResponseEntity<JSONObject> beaconGroupHasScenarioExceptionHandler(BeaconGroupHasScenarioException ex) {
        JSONObject responseBody = new JsonBuilder().add("reason", "scenario")
                                                   .add("error", ex.getLocalizedMessage())
                                                   .build();
        return new ResponseEntity<JSONObject>(responseBody, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(BeaconGroupDoesntHaveScenarioException.class)
    public ResponseEntity<JSONObject> beaconGroupDoesntHaveScenarioExceptionHandler(BeaconGroupDoesntHaveScenarioException ex) {
        JSONObject responseBody = new JsonBuilder().add("reason", "scenario")
                                                   .add("error", ex.getLocalizedMessage())
                                                   .build();
        return new ResponseEntity<JSONObject>(responseBody, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(BeaconWithGroupScenarioException.class)
    public ResponseEntity<JSONObject> beaconWithGroupScenarioExceptionHandler(BeaconWithGroupScenarioException ex) {
        JSONObject responseBody = new JsonBuilder().add("reason", "scenario")
                                                   .add("error", ex.getLocalizedMessage())
                                                   .build();
        return new ResponseEntity<JSONObject>(responseBody, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<JSONObject> constraintViolationExceptionHandler(ConstraintViolationException ex) {
        JSONObject responseBody = new JsonBuilder().add("reason", "scenario")
                                                   .add("error", "Constraint violation error ocurred! Unable to save scenario.")
                                                   .add("violations", formatViolations(ex.getConstraintViolations()))
                                                   .build();
        return new ResponseEntity<JSONObject>(responseBody, HttpStatus.BAD_REQUEST);
    }

    private JSONArray formatViolations(Set<ConstraintViolation<?>> violations) {
        JSONArray violationDescriptions = new JSONArray();
        for (ConstraintViolation<?> violation : violations) {
            Map<String, String> violationDescription = new HashMap<>();
            violationDescription.put("property", violation.getPropertyPath().toString());
            violationDescription.put("violation", violation.getMessage());
            violationDescriptions.add(violationDescription);
        }
        return violationDescriptions;
    }
}
