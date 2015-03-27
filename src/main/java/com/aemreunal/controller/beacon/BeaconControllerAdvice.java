package com.aemreunal.controller.beacon;

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
import com.aemreunal.exception.beacon.BeaconAlreadyExistsException;
import com.aemreunal.exception.beacon.BeaconNotFoundException;
import com.aemreunal.helper.json.JsonBuilderFactory;

@ControllerAdvice
public class BeaconControllerAdvice {
    @ExceptionHandler(BeaconNotFoundException.class)
    public ResponseEntity<JSONObject> beaconNotFoundExceptionHandler(BeaconNotFoundException ex) {
        JSONObject responseBody = JsonBuilderFactory.object().add("reason", "beacon")
                                                   .add("error", ex.getLocalizedMessage())
                                                   .build();
        return new ResponseEntity<JSONObject>(responseBody, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BeaconAlreadyExistsException.class)
    public ResponseEntity<JSONObject> beaconAlreadyExistsExceptionHandler(BeaconAlreadyExistsException ex) {
        JSONObject responseBody = JsonBuilderFactory.object().add("reason", "beacon")
                                                   .add("error", ex.getLocalizedMessage())
                                                   .build();
        return new ResponseEntity<JSONObject>(responseBody, HttpStatus.BAD_REQUEST);
    }
}
