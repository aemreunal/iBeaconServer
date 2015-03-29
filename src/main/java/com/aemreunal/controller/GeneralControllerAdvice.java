package com.aemreunal.controller;

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

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import com.aemreunal.exception.MalformedRequestException;
import com.aemreunal.helper.json.JsonArrayBuilder;
import com.aemreunal.helper.json.JsonBuilderFactory;

@ControllerAdvice
public class GeneralControllerAdvice {
    @ExceptionHandler(MalformedRequestException.class)
    public ResponseEntity<JSONObject> malformedRequestExceptionHandler(MalformedRequestException ex) {
        JSONObject responseBody = JsonBuilderFactory.object().add("reason", "request")
                                                    .add("error", ex.getLocalizedMessage())
                                                    .build();
        return new ResponseEntity<JSONObject>(responseBody, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<JSONObject> constraintViolationExceptionHandler(ConstraintViolationException ex) {
        JSONObject responseBody = JsonBuilderFactory.object().add("error", "Constraint violation error occurred! Unable to save scenario.")
                                                    .add("violations", formatViolations(ex.getConstraintViolations()))
                                                    .build();
        return new ResponseEntity<JSONObject>(responseBody, HttpStatus.BAD_REQUEST);
    }

    private JSONArray formatViolations(Set<ConstraintViolation<?>> violations) {
        JsonArrayBuilder arrayBuilder = JsonBuilderFactory.array();
        for (ConstraintViolation<?> violation : violations) {
            JSONObject jsonObject = JsonBuilderFactory.object().add("property", violation.getPropertyPath().toString())
                                                      .add("violation", violation.getMessage())
                                                      .build();
            arrayBuilder.add(jsonObject);
        }
        return arrayBuilder.build();
    }
}
