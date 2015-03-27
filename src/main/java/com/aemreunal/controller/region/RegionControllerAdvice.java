package com.aemreunal.controller.region;

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
import org.springframework.web.multipart.MultipartException;
import com.aemreunal.exception.region.*;
import com.aemreunal.helper.JsonBuilder;

@ControllerAdvice
public class RegionControllerAdvice {

    @ExceptionHandler(RegionNotFoundException.class)
    public ResponseEntity<JSONObject> regionNotFoundExceptionHandler(RegionNotFoundException ex) {
        return new ResponseEntity<JSONObject>(getErrorResponseBody(ex), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({ MapImageSaveException.class, MapImageLoadException.class, MapImageDeleteException.class })
    public ResponseEntity<JSONObject> internalErrorExceptionHandler(Exception ex) {
        return new ResponseEntity<JSONObject>(getErrorResponseBody(ex), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({ MultipartException.class, MultipartFileReadException.class })
    public ResponseEntity<JSONObject> multipartRequestExceptionHandler(MultipartException ex) {
        return new ResponseEntity<JSONObject>(getErrorResponseBody(ex), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(WrongFileTypeSubmittedException.class)
    public ResponseEntity<JSONObject> wrongFileTypeSubmittedExceptionHandler(WrongFileTypeSubmittedException ex) {
        return new ResponseEntity<JSONObject>(getErrorResponseBody(ex), HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    private JSONObject getErrorResponseBody(Exception ex) {
        return new JsonBuilder(JsonBuilder.OBJECT).addToObj("reason", "region")
                                .addToObj("error", ex.getLocalizedMessage())
                                .buildObj();
    }
}
