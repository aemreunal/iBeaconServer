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
import com.aemreunal.helper.JsonBuilder;

@ControllerAdvice
public class GeneralControllerAdvice {
    @ExceptionHandler(MalformedRequestException.class)
    public ResponseEntity<JSONObject> malformedRequestExceptionHandler(MalformedRequestException ex) {
        JSONObject responseBody = new JsonBuilder(JsonBuilder.OBJECT).addToObj("reason", "request")
                                                                     .addToObj("error", ex.getLocalizedMessage())
                                                                     .buildObj();
        return new ResponseEntity<JSONObject>(responseBody, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<JSONObject> constraintViolationExceptionHandler(ConstraintViolationException ex) {
        JSONObject responseBody = new JsonBuilder(JsonBuilder.OBJECT).addToObj("error", "Constraint violation error ocurred! Unable to save scenario.")
                                                                     .addToObj("violations", formatViolations(ex.getConstraintViolations()))
                                                                     .buildObj();
        return new ResponseEntity<JSONObject>(responseBody, HttpStatus.BAD_REQUEST);
    }

    private JSONArray formatViolations(Set<ConstraintViolation<?>> violations) {
        JsonBuilder arrayBuilder = new JsonBuilder(JsonBuilder.ARRAY);
        for (ConstraintViolation<?> violation : violations) {
            JSONObject jsonObject = new JsonBuilder(JsonBuilder.OBJECT).addToObj("property", violation.getPropertyPath().toString())
                                                                       .addToObj("violation", violation.getMessage())
                                                                       .buildObj();
            arrayBuilder.addToArr(jsonObject);
        }
        return arrayBuilder.buildArr();
    }
}
