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

import net.minidev.json.JSONArray;

import java.util.ArrayList;

public class JsonArrayBuilder {
    private ArrayList<Object> jsonList = new ArrayList<>();

    JsonArrayBuilder() {
    }

    public JsonArrayBuilder add(Object value) {
        jsonList.add(value);
        return this;
    }

    public JSONArray build() {
        JSONArray array = new JSONArray();
        array.addAll(jsonList);
        return array;
    }
}
