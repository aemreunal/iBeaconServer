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

import net.minidev.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class JsonObjectBuilder {
    private Map<String, Object> jsonMap = new HashMap<>();

    JsonObjectBuilder() {
    }

    public JsonObjectBuilder add(String key, Object value) {
        jsonMap.put(key, value);
        return this;
    }

    public JSONObject build() {
        return new JSONObject(jsonMap);
    }
}
