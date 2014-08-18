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

import org.apache.http.HttpStatus;
import com.aemreunal.config.GlobalSettings;
import com.aemreunal.domain.UserInfo;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.ValidatableResponse;

import static com.jayway.restassured.RestAssured.given;

public class EntityGetter {
    public static UserInfo getUser(String username) {
        JsonPath responseJson = getEntity("/" + username);
        return new UserInfo(responseJson.getString("username"), null, responseJson.getLong("userId"));
    }

    public static void failToGetUser(String username) {
        ValidatableResponse response = sendRequest("/" + username, HttpStatus.SC_NOT_FOUND);
    }

    private static JsonPath getEntity(String path) {
        ValidatableResponse response = sendRequest(path, HttpStatus.SC_OK);
        JsonPath responseJson = response.extract()
                                        .body()
                                        .jsonPath();
        System.out.println("Create response:");
        responseJson.prettyPrint();
        return responseJson;
    }

    private static ValidatableResponse sendRequest(String path, int httpStatus) {
        if (path.equals("")) {
            path = "/";
        }
        return given().log().ifValidationFails()

                      .when()
                      .get(GlobalSettings.BASE_CONTEXT_PATH + path)

                      .then()
                      .log().ifValidationFails()
                      .statusCode(httpStatus);
    }
}
