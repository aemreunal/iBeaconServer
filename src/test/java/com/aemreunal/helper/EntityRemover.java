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

import static com.jayway.restassured.RestAssured.given;

public class EntityRemover {
    public static void removeUser(String username) {
        removeEntity("/" + username);
    }

    private static void removeEntity(String path) {
        if (path.equals("")) {
            path = "/";
        }
        path += "?confirm=yes";
        given().log().ifValidationFails()

               .when()
               .delete(GlobalSettings.BASE_CONTEXT_PATH + path)

               .then()
               .log().ifValidationFails()
               .statusCode(HttpStatus.SC_OK);
    }

}
