package com.aemreunal.exception.scenario;

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

public class NoScenarioForQueryException extends NullPointerException {
    public NoScenarioForQueryException(String uuid, Integer major, Integer minor) {
        super("Beacon with UUID: " + uuid + ", Major: " + major + ", Minor: " + minor + " doesn't have an associated scenario!");
    }
}
