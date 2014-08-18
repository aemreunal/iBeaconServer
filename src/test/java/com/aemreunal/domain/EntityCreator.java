package com.aemreunal.domain;

import net.minidev.json.JSONObject;

import java.util.UUID;
import com.aemreunal.helper.RestHelper;
import com.jayway.restassured.path.json.JsonPath;

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

/*
 * https://code.google.com/p/rest-assured/
 *
 * Documentation: http://code.google.com/p/rest-assured/wiki/Usage
 */

public class EntityCreator {
    public static final String TEST_PASSWORD = "testpassword";

    /**
     * Sends an entity create POST request to the specified path, with the given {@link
     * net.minidev.json.JSONObject Json object} in the body of the request.
     *
     * @param entityAsJson
     *     The body of the request
     * @param path
     *     The path of the request
     *
     * @return The response Json
     */
    protected static JsonPath createEntity(JSONObject entityAsJson, String path) {
        JsonPath responseJson = RestHelper.createEntityRequest(entityAsJson, path);
        System.out.println("Create response:");
        responseJson.prettyPrint();
        return responseJson;
    }

    protected static String checkName(String name) {
        if (name.equals("")) {
            name = "testproject-" + UUID.randomUUID().toString();
        }
        return name;
    }

    protected static String checkDescription(String description, String name) {
        if (description.equals("")) {
            description = "Test project with name: " + name;
        }
        return description;
    }

    protected static String checkUsername(String username) {
        if (username.equals("")) {
            username = "testuser-" + UUID.randomUUID().toString();
        }
        return username;
    }

    protected static String checkPassword(String password) {
        if (password.equals("")) {
            password = TEST_PASSWORD;
        }
        return password;
    }
}
