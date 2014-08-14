package com.aemreunal.controller.project;

import net.minidev.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import com.aemreunal.exception.project.ProjectNotFoundException;

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
public class ProjectControllerAdvice {
    @ExceptionHandler(ProjectNotFoundException.class)
    public ResponseEntity<JSONObject> projectNotFoundExceptionHandler(ProjectNotFoundException ex) {
        Map<String, String> errorMessage = new HashMap<>(2);
        errorMessage.put("reason", "project");
        errorMessage.put("error", ex.getLocalizedMessage());
        JSONObject responseBody = new JSONObject(errorMessage);
        return new ResponseEntity<JSONObject>(responseBody, HttpStatus.BAD_REQUEST);
    }
}
