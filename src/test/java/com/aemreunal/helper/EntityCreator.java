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

/*
 * https://code.google.com/p/rest-assured/
 *
 * Documentation: http://code.google.com/p/rest-assured/wiki/Usage
 */

import net.minidev.json.JSONObject;

import java.util.UUID;
import org.apache.http.HttpStatus;
import com.aemreunal.config.GlobalSettings;
import com.aemreunal.domain.ProjectInfo;
import com.aemreunal.domain.UserInfo;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.path.json.JsonPath;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;

public class EntityCreator {
    public static final String TEST_PASSWORD = "testpassword";

//    private static BCryptPasswordEncoder encoder    = new BCryptPasswordEncoder(GlobalSettings.BCRYPT_LOG_FACTOR);

    /**
     * Calls {@link com.aemreunal.helper.EntityCreator#createRandomUser(String, String)
     * createRandomUser(String, String)} with empty String arguments.
     *
     * @return The created user's info.
     *
     * @see com.aemreunal.helper.EntityCreator#createRandomUser(String, String)
     */
    public static UserInfo createRandomUser() {
        return createRandomUser("", "");
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
     *     password of "{@value EntityCreator#TEST_PASSWORD}" will be assigned.
     *
     * @return The {@link com.aemreunal.domain.UserInfo info} of the created user.
     */
    public static UserInfo createRandomUser(String username, String password) {
        username = checkUsername(username);
        password = checkPassword(password);

        JSONObject userJson = new JsonBuilder().add("username", username)
                                               .add("password", password)
                                               .build();
        String path = GlobalSettings.USER_PATH_MAPPING;
        JsonPath responseJson = createEntity(userJson, path);

        assertEquals("Requested username and response username do not match!", username, responseJson.getString("username"));
        // TODO Assert in-DB password matches
        // assertTrue(encoder.matches(password, responseJson.getString("password")));
        return new UserInfo(username, password, responseJson.getLong("userId"));
    }

    /**
     * Calls {@link com.aemreunal.helper.EntityCreator#createRandomProject(String, String,
     * String) createRandomProject(String, String, String)} with empty String arguments,
     * except for the username argument, which must be supplied.
     *
     * @param ownerUsername
     *     The username of the owner of the project. Must be supplied.
     *
     * @return The created project's info.
     *
     * @see com.aemreunal.helper.EntityCreator#createRandomProject(String, String, String)
     */
    public static ProjectInfo createRandomProject(String ownerUsername) {
        return createRandomProject(ownerUsername, "", "");
    }

    /**
     * Creates a random project and returns the info of the project.
     * <p/>
     * Request path: {@value com.aemreunal.config.GlobalSettings#PROJECT_PATH_MAPPING}<br>
     * Request body: <pre>{@code
     * {
     *      "name":"&lt;Project name&gt;",
     *      "description":"&lt;Project description&gt;"
     * }}</pre>
     *
     * @param ownerUsername
     *     The username of the owner of the project. Must be supplied.
     * @param name
     *     (Optional) The requested name of the project. If an empty name is supplied, a
     *     random name will be generated.
     * @param description
     *     (Optional) The requested description of the project. If an empty description is
     *     supplied, a random description will be generated.
     *
     * @return The created project's info
     */
    public static ProjectInfo createRandomProject(String ownerUsername, String name, String description) {
        name = checkName(name);
        description = checkDescription(description, name);

        JSONObject projectJson = new JsonBuilder().add("name", name)
                                                  .add("description", description)
                                                  .build();
        String path = GlobalSettings.USER_PATH_MAPPING + "/" + ownerUsername + "/project";
        JsonPath responseJson = createEntity(projectJson, path);

        assertEquals("Requested project name and response project name do not match!", name, responseJson.getString("name"));
        assertEquals("Requested project description and response project description do not match!", description, responseJson.getString("description"));
        return new ProjectInfo(ownerUsername, responseJson.getLong("projectId"), responseJson.getString("secret"), name, description);
    }

    private static JsonPath createEntity(JSONObject entityAsJson, String path) {
        JsonPath responseJson = given().contentType(ContentType.JSON)
                                       .log().ifValidationFails()

                                       .when()
                                       .body(entityAsJson)
                                       .post(path)

                                       .then()
                                       .log().ifValidationFails()
                                       .statusCode(HttpStatus.SC_CREATED)

                                       .extract()
                                       .body()
                                       .jsonPath();
        responseJson.prettyPrint();
        return responseJson;
    }

    private static JsonPath getEntity(String path) {
        JsonPath responseJson = given().log().ifValidationFails()

                                       .when()
                                       .get(path)

                                       .then()
                                       .log().ifValidationFails()
                                       .statusCode(HttpStatus.SC_OK)
                                       .contentType(ContentType.JSON)

                                       .extract()
                                       .body()
                                       .jsonPath();
        responseJson.prettyPrint();
        return responseJson;
    }

    private static String checkUsername(String username) {
        if (username.equals("")) {
            username = "testuser-" + UUID.randomUUID().toString();
        }
        return username;
    }

    private static String checkPassword(String password) {
        if (password.equals("")) {
            password = TEST_PASSWORD;
        }
        return password;
    }

    private static String checkName(String name) {
        if (name.equals("")) {
            name = "testproject-" + UUID.randomUUID().toString();
        }
        return name;
    }

    private static String checkDescription(String description, String name) {
        if (description.equals("")) {
            description = "Test project with name: " + name;
        }
        return description;
    }
}
