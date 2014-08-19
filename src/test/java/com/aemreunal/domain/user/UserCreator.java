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

import net.minidev.json.JSONObject;

import org.apache.http.HttpStatus;
import com.aemreunal.config.GlobalSettings;
import com.aemreunal.domain.EntityCreator;
import com.aemreunal.helper.JsonBuilder;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.ValidatableResponse;

import static org.junit.Assert.*;

public class UserCreator extends EntityCreator {
    public static final String USER_CREATE_PATH = GlobalSettings.USER_PATH_MAPPING + GlobalSettings.USER_CREATE_MAPPING;

//    private static BCryptPasswordEncoder encoder    = new BCryptPasswordEncoder(GlobalSettings.BCRYPT_LOG_FACTOR);

    /**
     * Calls {@link UserCreator#createUser(String, String) createUser(String, String)}
     * with empty String arguments.
     *
     * @return The created user's info.
     *
     * @see UserCreator#createUser(String, String)
     */
    public static UserInfo createRandomUser() {
        return createUser("", "");
    }

    /**
     * Creates a random user and returns the info of the user.
     * <p/>
     * Request path: {@value com.aemreunal.config.GlobalSettings#USER_PATH_MAPPING}<br>
     * Request body: <pre>{@code
     * {
     *      "username":"&lt;User username&gt;",
     *      "password":"&lt;User password&gt;"
     * }}</pre>
     *
     * @param username
     *     (Optional) The requested username of the user. If left blank, a random username
     *     will be generated.
     * @param password
     *     (Optional) The requested password of the user. If left blank, the default
     *     password of "{@value UserCreator#TEST_PASSWORD}" will be assigned.
     *
     * @return The {@link UserInfo info} of the created user.
     */
    public static UserInfo createUser(String username, String password) {
        username = checkUsername(username);
        password = checkPassword(password);

        JSONObject userJson = getUserCreateJson(username, password);
        JsonPath responseJson = createEntity(userJson, USER_CREATE_PATH);

        assertEquals("Requested username and response username do not match!", username, responseJson.getString("username"));
        // TODO Assert in-DB password matches
        // assertTrue(encoder.matches(password, responseJson.getString("password")));
        return new UserInfo(username, password, responseJson.getLong("userId"));
    }

    public static void failToCreateUser(String username) {
        JSONObject userJson = getUserCreateJson(username, TEST_PASSWORD);
        ValidatableResponse response = sendPostRequest(userJson, USER_CREATE_PATH, HttpStatus.SC_BAD_REQUEST);
        JsonPath jsonResponse = response.extract().body().jsonPath();
        assertNotNull(jsonResponse.getString("error"));
        assertNotEquals(jsonResponse.getString("error"), "");
        assertNotNull(jsonResponse.getString("reason"));
        assertNotEquals(jsonResponse.getString("reason"), "");
        // TODO add violations assertTrue(jsonResponse.getList("violations").size() >= 1);
        jsonResponse.prettyPrint();
    }

    private static JSONObject getUserCreateJson(String username, String password) {
        return new JsonBuilder().add("username", username)
                                .add("password", password)
                                .build();
    }
}
