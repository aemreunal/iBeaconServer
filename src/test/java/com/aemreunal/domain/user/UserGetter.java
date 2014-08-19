package com.aemreunal.domain.user;

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
import com.aemreunal.domain.EntityGetter;
import com.jayway.restassured.path.json.JsonPath;

public class UserGetter extends EntityGetter {
    public static UserInfo getUser(String username) {
        JsonPath responseJson = getEntity("/" + username);
        return new UserInfo(responseJson.getString("username"), null, responseJson.getLong("userId"));
    }

    public static void failToFindUser(String username) {
        sendGetRequest("/" + username, HttpStatus.SC_NOT_FOUND);
    }

    public static void failToGetUser(String username) {
        sendGetRequest("/" + username, HttpStatus.SC_BAD_REQUEST);
    }
}
