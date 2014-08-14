package com.aemreunal.exception.user;

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

public class UsernameClashException extends IllegalArgumentException {

    public UsernameClashException(String causingUsername) {
        super("Username \'" + causingUsername + "\' is already taken. Please choose another username." );
    }
}
