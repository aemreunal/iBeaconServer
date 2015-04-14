package com.aemreunal.exception.connection;

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

public class ConnectionNotPossibleException extends Exception {
    public ConnectionNotPossibleException(Long beaconId) {
        super("Unable to create connection, beacon " + beaconId + " is not a designated beacon!");
    }

    public ConnectionNotPossibleException() {
        super("Unable to create connection, a beacon can't be connected to itself!");
    }
}
