package com.aemreunal.exception;

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
public class MalformedRequestException extends NullPointerException {

    public MalformedRequestException(String objectName) {
        super("Your request to create an object of type \'" + objectName + "\' was malformed. Please try again.");
    }
}

