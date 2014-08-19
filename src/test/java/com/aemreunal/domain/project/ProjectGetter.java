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

import java.util.ArrayList;
import java.util.HashMap;
import org.apache.http.HttpStatus;
import com.aemreunal.domain.EntityGetter;
import com.jayway.restassured.path.json.JsonPath;

public class ProjectGetter extends EntityGetter {
    public static ArrayList<ProjectInfo> getAllProjects(String username) {
        JsonPath responseJson = getEntity("/" + username + "/project");
        ArrayList<ProjectInfo> projects = new ArrayList<>();
        for (HashMap projectMap : responseJson.getList("", HashMap.class)) {
            projects.add(new ProjectInfo(projectMap, username, ""));
        }
        return projects;
    }

    public static void failToGetAllProjects(String username) {
        sendGetRequest("/" + username + "/project", HttpStatus.SC_NOT_FOUND);
    }

    public static ProjectInfo findProject(String username, Long projectId) {
        JsonPath responseJson = getEntity("/" + username + "/project/" + projectId);
        return new ProjectInfo(responseJson, username, "");
    }

    public static void failToFindProject(String username, Long projectId) {
        // TODO parametrise requests via rest-assured get()/post() Object... parameters
        sendGetRequest("/" + username + "/project/" + projectId, HttpStatus.SC_NOT_FOUND);
    }
}
