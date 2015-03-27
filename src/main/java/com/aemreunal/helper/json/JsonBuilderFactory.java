package com.aemreunal.helper.json;

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

public class JsonBuilderFactory {
    public static JsonObjectBuilder object() {
        return new JsonObjectBuilder();
    }

    public static JsonArrayBuilder array() {
        return new JsonArrayBuilder();
    }
}

