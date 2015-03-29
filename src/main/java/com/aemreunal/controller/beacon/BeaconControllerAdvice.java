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
import com.aemreunal.exception.connection.BeaconIsNotDesignatedException;
import com.aemreunal.exception.connection.ConnectionExistsException;
import com.aemreunal.exception.connection.ConnectionNotFoundException;
import com.aemreunal.helper.json.JsonBuilderFactory;

@ControllerAdvice
public class BeaconControllerAdvice {
    @ExceptionHandler(BeaconNotFoundException.class)
    public ResponseEntity<JSONObject> beaconNotFoundExceptionHandler(BeaconNotFoundException ex) {
        return getResponse(ex, "beacon", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BeaconAlreadyExistsException.class)
    public ResponseEntity<JSONObject> beaconAlreadyExistsExceptionHandler(BeaconAlreadyExistsException ex) {
        return getResponse(ex, "beacon", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConnectionExistsException.class)
    public ResponseEntity<JSONObject> connectionExistsExceptionHandler(ConnectionExistsException ex) {
        return getResponse(ex, "connection", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConnectionNotFoundException.class)
    public ResponseEntity<JSONObject> connectionNotFoundExceptionHandler(ConnectionNotFoundException ex) {
        return getResponse(ex, "connection", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BeaconIsNotDesignatedException.class)
    public ResponseEntity<JSONObject> beaconIsNotDesignatedExceptionHandler(BeaconIsNotDesignatedException ex) {
        return getResponse(ex, "connection", HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<JSONObject> getResponse(Exception ex, String reason, HttpStatus status) {
        JSONObject responseBody = JsonBuilderFactory.object().add("reason", reason)
                                                    .add("error", ex.getLocalizedMessage())
                                                    .build();
        return new ResponseEntity<JSONObject>(responseBody, status);
    }
}
