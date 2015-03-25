package com.aemreunal.exception;

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

public class MalformedRequestException /*extends Throwable*/ extends RuntimeException {

    // http://normanmaurer.me/blog/2013/11/09/The-hidden-performance-costs-of-instantiating-Throwables/
//    public MalformedRequestException() {
//        super("Your request is malformed. Please try again.", null, true, false);
//    }

    public MalformedRequestException() {
        super("Your request is malformed. Please try again.");
    }
}

