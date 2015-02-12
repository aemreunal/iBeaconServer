package com.aemreunal.controller.region;

import net.minidev.json.JSONObject;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import com.aemreunal.exception.region.BeaconHasRegionException;
import com.aemreunal.exception.region.RegionNotFoundException;
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
public class RegionControllerAdvice {

    @ExceptionHandler(RegionNotFoundException.class)
    public ResponseEntity<JSONObject> regionNotFoundExceptionHandler(RegionNotFoundException ex) {
        JSONObject responseBody = new JsonBuilder().add("reason", "region")
                                                   .add("error", ex.getLocalizedMessage())
                                                   .build();
        return new ResponseEntity<JSONObject>(responseBody, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BeaconHasRegionException.class)
    public ResponseEntity<JSONObject> beaconHasRegionExceptionHandler(BeaconHasRegionException ex) {
        JSONObject responseBody = new JsonBuilder().add("reason", "region")
                                                   .add("error", ex.getLocalizedMessage())
                                                   .build();
        return new ResponseEntity<JSONObject>(responseBody, HttpStatus.BAD_REQUEST);
    }
}
