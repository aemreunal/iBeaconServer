package com.aemreunal.helper;

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

import net.minidev.json.JSONObject;

import org.apache.http.HttpStatus;
import com.aemreunal.config.GlobalSettings;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.ValidatableResponse;

import static com.jayway.restassured.RestAssured.given;

public class RestHelper {

    public static JsonPath createEntityRequest(JSONObject entityAsJson, String path) {
        return RestHelper.sendPostRequest(entityAsJson, path, HttpStatus.SC_CREATED)
                         .extract()
                         .body()
                         .jsonPath();
    }

    public static JsonPath getEntityRequest(String path) {
        return RestHelper.sendGetRequest(path, HttpStatus.SC_OK)
                         .extract()
                         .body()
                         .jsonPath();
    }

    public static JsonPath deleteEntityRequest(String path) {
        return RestHelper.sendDeleteRequest(path, HttpStatus.SC_OK)
                         .extract()
                         .body()
                         .jsonPath();
    }

    public static ValidatableResponse sendGetRequest(String path, int expectedHttpStatus) {
        if (path.equals("")) {
            path = "/";
        }
        return given().log().ifValidationFails()

                      .when()
                      .get(GlobalSettings.BASE_CONTEXT_PATH + path)

                      .then()
                      .log().ifValidationFails()
                      .statusCode(expectedHttpStatus);
    }

    public static ValidatableResponse sendPostRequest(JSONObject entityAsJson, String path, int expectedHttpStatus) {
        if (path.equals("")) {
            path = "/";
        }
        return given().contentType(ContentType.JSON)
                      .log().ifValidationFails()

                      .when()
                      .body(entityAsJson)
                      .post(GlobalSettings.BASE_CONTEXT_PATH + path)

                      .then()
                      .log().ifValidationFails()
                      .statusCode(expectedHttpStatus);
    }

    public static ValidatableResponse sendDeleteRequest(String path, int expectedHttpStatus) {
        if (path.equals("")) {
            path = "/";
        }
        return given().log().ifValidationFails()

                      .when()
                      .delete(GlobalSettings.BASE_CONTEXT_PATH + path)

                      .then()
                      .log().ifValidationFails()
                      .statusCode(expectedHttpStatus);
    }
}
