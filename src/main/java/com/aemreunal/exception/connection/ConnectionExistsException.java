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

public class ConnectionExistsException extends Exception {
    public ConnectionExistsException(Long beaconOneId, Long beaconTwoId) {
        super("There already is a connection between Beacon " + beaconOneId + " and Beacon " + beaconTwoId + "!");
    }
}
