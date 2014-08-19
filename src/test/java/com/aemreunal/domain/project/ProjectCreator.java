package com.aemreunal.domain.project;

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

public class ProjectCreator extends EntityCreator {
    /**
     * Calls {@link com.aemreunal.domain.project.ProjectCreator#createProject(String,
     * String, String) createProject(String, String, String)} with empty String arguments,
     * except for the username argument, which must be supplied.
     *
     * @param ownerUsername
     *     The username of the owner of the project. Must be supplied.
     *
     * @return The created project's info.
     *
     * @see com.aemreunal.domain.project.ProjectCreator#createProject(String, String,
     * String)
     */
    public static ProjectInfo createRandomProject(String ownerUsername) {
        return createProject(ownerUsername, "", "");
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
    public static ProjectInfo createProject(String ownerUsername, String name, String description) {
        name = checkName(name);
        description = checkDescription(description, name);

        JSONObject projectJson = getProjectCreateJson(name, description);
        String path = getProjectCreatePath(ownerUsername);
        JsonPath responseJson = createEntity(projectJson, path);

        assertEquals("Requested project name and response project name do not match!", name, responseJson.getString("name"));
        assertEquals("Requested project description and response project description do not match!", description, responseJson.getString("description"));
        return new ProjectInfo(responseJson, ownerUsername, responseJson.getString("secret"));
    }

    public static void failToCreateProject(String ownerUsername, String name, String description) {
        name = checkName(name);
        description = checkDescription(description, name);

        JSONObject projectJson = getProjectCreateJson(name, description);
        String path = getProjectCreatePath(ownerUsername);
        ValidatableResponse response = sendPostRequest(projectJson, path, HttpStatus.SC_BAD_REQUEST);

        JsonPath jsonResponse = response.extract().body().jsonPath();
        assertNotNull(jsonResponse.getString("error"));
        assertNotEquals(jsonResponse.getString("error"), "");
        assertNotNull(jsonResponse.getString("reason"));
        assertEquals(jsonResponse.getString("reason"), "project");
        assertTrue(jsonResponse.getList("violations").size() >= 1);
        jsonResponse.prettyPrint();
    }

    private static JSONObject getProjectCreateJson(String name, String description) {
        return new JsonBuilder().add("name", name)
                                .add("description", description)
                                .build();
    }

    private static String getProjectCreatePath(String ownerUsername) {
        return GlobalSettings.USER_PATH_MAPPING + "/" + ownerUsername + "/project";
    }
}
