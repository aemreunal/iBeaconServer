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

import java.util.UUID;
import org.apache.http.HttpStatus;
import com.aemreunal.config.GlobalSettings;
import com.aemreunal.domain.EntityCreator;
import com.aemreunal.domain.Project;
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
        name = checkProjectName(name);
        description = checkDescription(description);

        JSONObject projectJson = getProjectCreateJson(name, description);
        String path = getProjectCreatePath(ownerUsername);
        JsonPath responseJson = createEntity(projectJson, path);

        assertEquals("Requested project name and response project name do not match!", name, responseJson.getString("name"));
        assertEquals("Requested project description and response project description do not match!", description, responseJson.getString("description"));
        return new ProjectInfo(responseJson, ownerUsername, responseJson.getString("secret"));
    }

    public static void failToCreateProjectWithBadUsername(String ownerUsername, String name, String description, boolean usernameIsLegal) {
        JsonPath jsonResponse;
        if (usernameIsLegal) {
            jsonResponse = failToCreateProject(ownerUsername, name, description, HttpStatus.SC_NOT_FOUND);
        } else {
            jsonResponse = failToCreateProject(ownerUsername, name, description, HttpStatus.SC_BAD_REQUEST);
        }
        assertNotNull(jsonResponse.getString("error"));
        assertNotEquals(jsonResponse.getString("error"), "");
        assertNotNull(jsonResponse.getString("reason"));
        assertEquals(jsonResponse.getString("reason"), "username");
        jsonResponse.prettyPrint();
    }

    public static void failToCreateInvalidProject(String ownerUsername, String name, String description) {
        JsonPath jsonResponse = failToCreateProject(ownerUsername, name, description, HttpStatus.SC_BAD_REQUEST);
        assertNotNull(jsonResponse.getString("error"));
        assertNotEquals(jsonResponse.getString("error"), "");
        assertNotNull(jsonResponse.getString("reason"));
        assertEquals(jsonResponse.getString("reason"), "project");
        assertTrue("Violations are not returned!", jsonResponse.getList("violations").size() >= 1);
        jsonResponse.prettyPrint();
    }

    public static JsonPath failToCreateProject(String ownerUsername, String name, String description, int httpStatus) {
        JSONObject projectJson = getProjectCreateJson(name, description);
        String path = getProjectCreatePath(ownerUsername);
        ValidatableResponse response;
        response = sendPostRequest(projectJson, path, httpStatus);

        JsonPath jsonResponse = response.extract().body().jsonPath();
        jsonResponse.prettyPrint();
        return jsonResponse;
    }

    protected static String checkProjectName(String name) {
        if (name.equals("")) {
            name = "testproject-" + UUID.randomUUID().toString();
        } else {
            String errorMessage = "A project name with less than " + Project.NAME_MAX_LENGTH + " characters must be provided!";
            assertTrue(errorMessage, name.length() <= Project.NAME_MAX_LENGTH);
        }
        return name;
    }

    private static JSONObject getProjectCreateJson(String name, String description) {
        return new JsonBuilder().add("name", name)
                                .add("description", description)
                                .build();
    }

    private static String getProjectCreatePath(String ownerUsername) {
        return GlobalSettings.USER_PATH_MAPPING + "/" + ownerUsername + "/projects";
    }
}
