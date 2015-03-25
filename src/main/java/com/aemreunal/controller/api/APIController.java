package com.aemreunal.controller.api;

/*
 * *********************** *
 * Copyright (c) 2015      *
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
 * *********************** *
 */

import net.minidev.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.aemreunal.config.GlobalSettings;
import com.aemreunal.domain.Project;
import com.aemreunal.domain.Scenario;
import com.aemreunal.exception.MalformedRequestException;
import com.aemreunal.service.ProjectService;
import com.aemreunal.service.ScenarioService;

@Controller
@RequestMapping(GlobalSettings.API_PATH_MAPPING)
public class APIController {
    @Autowired
    private ProjectService projectService;

    @Autowired
    private ScenarioService scenarioService;

    // TODO get entire project info via secret
    // Update the database on client app launch
    // 1) Get available regions
    // 1.1) If new regions are present, get them, their beacons, their images
    // 2) Check for updates to existing regions
    // 2.1) If updates are found, download them (like 1.1).

    /*
     * Project query JSON example:
     * {
     *      "projectId": <project ID>
     *      "secret":    <project Secret>
     * }
     */
    @RequestMapping(method = RequestMethod.POST, value = GlobalSettings.API_PROJECT_QUERY_PATH_MAPPING, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Project> queryForProject(@RequestBody JSONObject projectQueryJson) {
        verifyProjectQueryRequest(projectQueryJson);
        Project project = getProject(projectQueryJson);
        return new ResponseEntity<Project>(project, HttpStatus.OK);
    }

    private void verifyProjectQueryRequest(JSONObject projectQueryJson) {
        if (!projectQueryJson.containsKey("projectId") ||
                !projectQueryJson.containsKey("secret")) {
            throw new MalformedRequestException();
        }
    }

    private Project getProject(JSONObject projectQueryJson) {
        Long projectId = Long.valueOf(projectQueryJson.get("projectId").toString());
        String secret = projectQueryJson.get("secret").toString().toUpperCase();
        return projectService.queryForProject(projectId, secret);
    }

    /*
     * Beacon query JSON example:
     * {
     *      "uuid":   <beacon UUID>
     *      "major":  <beacon Major>
     *      "minor":  <beacon Minor>
     *      "secret": <project Secret>
     * }
     */
    @RequestMapping(method = RequestMethod.POST, value = GlobalSettings.API_BEACON_QUERY_PATH_MAPPING, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JSONObject> queryForScenario(@RequestBody JSONObject beaconQueryJson) {
        verifyBeaconQueryRequest(beaconQueryJson);
        Scenario scenario = getScenario(beaconQueryJson);
        return new ResponseEntity<JSONObject>(scenario.generateQueryResponse(), HttpStatus.OK);
    }

    private void verifyBeaconQueryRequest(JSONObject beaconQueryJson) {
        if (!beaconQueryJson.containsKey("uuid") ||
                !beaconQueryJson.containsKey("major") ||
                !beaconQueryJson.containsKey("minor") ||
                !beaconQueryJson.containsKey("secret")) {
            throw new MalformedRequestException();
        }
    }

    private Scenario getScenario(JSONObject beaconQueryJson) {
        String uuid = beaconQueryJson.get("uuid").toString().toUpperCase();
        Integer major = Integer.valueOf(beaconQueryJson.get("major").toString());
        Integer minor = Integer.valueOf(beaconQueryJson.get("minor").toString());
        String secret = beaconQueryJson.get("secret").toString().toUpperCase();
        return scenarioService.queryForScenario(uuid, major, minor, secret);
    }
}
