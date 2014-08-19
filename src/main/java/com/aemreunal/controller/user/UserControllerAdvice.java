package com.aemreunal.controller.user;

import net.minidev.json.JSONObject;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import com.aemreunal.exception.user.InvalidUsernameException;
import com.aemreunal.exception.user.UserNotFoundException;
import com.aemreunal.exception.user.UsernameClashException;
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
public class UserControllerAdvice {
    @ExceptionHandler(UsernameClashException.class)
    public ResponseEntity<JSONObject> usernameClashExceptionHandler(UsernameClashException ex) {
        JSONObject responseBody = new JsonBuilder().add("reason", "username")
                                                   .add("error", ex.getLocalizedMessage())
                                                   .build();
        return new ResponseEntity<JSONObject>(responseBody, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidUsernameException.class)
    public ResponseEntity<JSONObject> invalidUsernameExceptionHandler(InvalidUsernameException ex) {
        JSONObject responseBody = new JsonBuilder().add("reason", "username")
                                                   .add("error", ex.getLocalizedMessage())
                                                   .build();
        return new ResponseEntity<JSONObject>(responseBody, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<JSONObject> userNotFoundExceptionHandler(UserNotFoundException ex) {
        JSONObject responseBody = new JsonBuilder().add("reason", "username")
                                                   .add("error", ex.getLocalizedMessage())
                                                   .build();
        return new ResponseEntity<JSONObject>(responseBody, HttpStatus.NOT_FOUND);
    }
}
