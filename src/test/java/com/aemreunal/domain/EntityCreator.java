package com.aemreunal.domain;

import net.minidev.json.JSONObject;

import java.util.Random;
import java.util.UUID;
import com.aemreunal.helper.RestHelper;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.ValidatableResponse;

import static org.junit.Assert.assertTrue;

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

public class EntityCreator extends RestHelper {
    public static final String TEST_PASSWORD = "testpassword";
    protected static    Random random        = new Random();

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
        JsonPath responseJson = createEntityRequest(entityAsJson, path);
        System.out.println("Create response:");
        responseJson.prettyPrint();
        return responseJson;
    }

    protected static JsonPath failToCreateEntity(String path, int httpStatus, JSONObject entityJson) {
        ValidatableResponse response = sendPostRequest(entityJson, path, httpStatus);
        JsonPath jsonResponse = response.extract().body().jsonPath();
        jsonResponse.prettyPrint();
        return jsonResponse;
    }

    protected static String checkDescription(String description) {
        if (description.equals("")) {
            description = "Test description! This is a test object.";
        } else {
            String errorMessage = "A description with less than " + Project.DESCRIPTION_MAX_LENGTH + " characters must be provided!";
            assertTrue(errorMessage, description.length() <= Project.DESCRIPTION_MAX_LENGTH);
        }
        return description;
    }

    protected static String checkUsername(String username) {
        if (username.equals("")) {
            username = "testuser" + UUID.randomUUID().toString().replaceAll("[^a-zA-Z0-9]", "");
        } else {
            String errorMessage = "A username with less than " + User.USERNAME_MAX_LENGTH + " characters must be provided!";
            assertTrue(errorMessage, username.length() <= User.USERNAME_MAX_LENGTH);
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
