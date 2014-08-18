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
import com.aemreunal.domain.UserInfo;
import com.jayway.restassured.path.json.JsonPath;

public class EntityGetter {
    public static UserInfo getUser(String username) {
        JsonPath responseJson = getEntity("/" + username);
        return new UserInfo(responseJson.getString("username"), null, responseJson.getLong("userId"));
    }

    public static void failToGetUser(String username) {
        RestHelper.sendGetRequest("/" + username, HttpStatus.SC_NOT_FOUND);
    }

    private static JsonPath getEntity(String path) {
        JsonPath responseJson = RestHelper.getEntityRequest(path);
        System.out.println("Get response:");
        responseJson.prettyPrint();
        return responseJson;
    }
}
