package com.aemreunal.helper;

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
import net.minidev.json.JSONObject;

import java.util.*;

public class JsonBuilder {
    public static final int OBJECT = 0;
    public static final int ARRAY  = 1;

    private Map<String, Object> jsonMap;

    private ArrayList<Object> jsonList;

    public JsonBuilder(int jsonToCreate) {
        switch (jsonToCreate) {
            case OBJECT:
                jsonMap = new HashMap<>();
                break;
            case ARRAY:
                jsonList = new ArrayList<>();
                break;
        }
    }

    public JsonBuilder addToArr(Object value) {
        jsonList.add(value);
        return this;
    }

    public JsonBuilder addToObj(String key, Object value) {
        jsonMap.put(key, value);
        return this;
    }

    public JSONObject buildObj() {
        return new JSONObject(jsonMap);
    }

    public JSONArray buildArr() {
        JSONArray array = new JSONArray();
        array.addAll(jsonList);
        return array;
    }
}
