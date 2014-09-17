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

    public MalformedRequestException() {
        super("Your request is malformed. Please try again.");
    }
}

