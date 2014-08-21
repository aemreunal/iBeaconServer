package com.aemreunal.controller.beaconGroup;

import net.minidev.json.JSONObject;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import com.aemreunal.exception.beaconGroup.BeaconGroupNotFoundException;
import com.aemreunal.exception.beaconGroup.BeaconHasGroupException;
import com.aemreunal.helper.JsonBuilder;

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

@ControllerAdvice
public class BeaconGroupControllerAdvice {

    @ExceptionHandler(BeaconGroupNotFoundException.class)
    public ResponseEntity<JSONObject> beaconGroupNotFoundExceptionHandler(BeaconGroupNotFoundException ex) {
        JSONObject responseBody = new JsonBuilder().add("reason", "beacongroup")
                                                   .add("error", ex.getLocalizedMessage())
                                                   .build();
        return new ResponseEntity<JSONObject>(responseBody, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BeaconHasGroupException.class)
    public ResponseEntity<JSONObject> beaconHasGroupExceptionHandler(BeaconHasGroupException ex) {
        JSONObject responseBody = new JsonBuilder().add("reason", "beacongroup")
                                                   .add("error", ex.getLocalizedMessage())
                                                   .build();
        return new ResponseEntity<JSONObject>(responseBody, HttpStatus.BAD_REQUEST);
    }
}
