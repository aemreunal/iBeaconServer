package com.aemreunal.controller.project;

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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import com.aemreunal.exception.project.ProjectNotFoundException;
import com.aemreunal.helper.JsonBuilder;

@ControllerAdvice
public class ProjectControllerAdvice {
    @ExceptionHandler(ProjectNotFoundException.class)
    public ResponseEntity<JSONObject> projectNotFoundExceptionHandler(ProjectNotFoundException ex) {
        JSONObject responseBody = new JsonBuilder(JsonBuilder.OBJECT).addToObj("reason", "project")
                                                   .addToObj("error", ex.getLocalizedMessage())
                                                   .buildObj();
        return new ResponseEntity<JSONObject>(responseBody, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = { ConstraintViolationException.class, TransactionSystemException.class })
    public ResponseEntity<JSONObject> constraintViolationExceptionHandler(ConstraintViolationException ex) {
        JSONObject responseBody = new JsonBuilder(JsonBuilder.OBJECT).addToObj("reason", "project")
                                                   .addToObj("error", "Constraint violation error ocurred! Unable to save project.")
                                                   .addToObj("violations", formatViolations(ex.getConstraintViolations()))
                                                   .buildObj();
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
