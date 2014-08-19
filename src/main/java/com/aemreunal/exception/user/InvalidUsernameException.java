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

public class InvalidUsernameException extends IllegalArgumentException  {
    public InvalidUsernameException(String causingUsername, String reason) {
        super("Username \'" + causingUsername + "\' is invalid! " + reason);
    }
}
