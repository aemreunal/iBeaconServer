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
import java.util.Collection;

public class JsonArrayBuilder {
    private ArrayList<Object> jsonList = new ArrayList<>();

    JsonArrayBuilder() {
    }

    public JsonArrayBuilder add(Object item) {
        jsonList.add(item);
        return this;
    }

    public JsonArrayBuilder addAll(Collection items) {
        jsonList.addAll(items);
        return this;
    }

    public JSONArray build() {
        JSONArray array = new JSONArray();
        array.addAll(jsonList);
        return array;
    }
}
