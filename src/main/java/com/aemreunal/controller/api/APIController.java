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

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
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
import com.aemreunal.domain.Region;
import com.aemreunal.domain.Scenario;
import com.aemreunal.exception.MalformedRequestException;
import com.aemreunal.service.APIService;
import com.aemreunal.service.ScenarioService;

@Controller
@RequestMapping(GlobalSettings.API_PATH_MAPPING)
public class APIController {
    @Autowired
    private APIService apiService;

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
     *      "projectId": <project ID>,
     *      "secret":    <project Secret>
     * }
     */
    @RequestMapping(method = RequestMethod.POST, value = GlobalSettings.API_PROJECT_QUERY_PATH_MAPPING, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JSONObject> queryForProject(@RequestBody JSONObject projectQueryJson) {
        verifyProjectQueryRequest(projectQueryJson);
        JSONObject project = getProject(projectQueryJson);
        return new ResponseEntity<JSONObject>(project, HttpStatus.OK);
    }


    @RequestMapping(method = RequestMethod.POST, value = GlobalSettings.API_REGION_QUERY_PATH_MAPPING, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LinkedHashSet<JSONObject>> queryForRegions(@RequestBody JSONObject projectQueryJson) {
        verifyProjectQueryRequest(projectQueryJson);
        LinkedHashSet<JSONObject> regions = getRegionsOfProject(projectQueryJson);
        return new ResponseEntity<LinkedHashSet<JSONObject>>(regions, HttpStatus.OK);
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

    private JSONObject getProject(JSONObject projectQueryJson) {
        Long projectId = Long.valueOf(projectQueryJson.get("projectId").toString());
        String secret = projectQueryJson.get("secret").toString().toUpperCase();
        Project project = apiService.queryForProject(projectId, secret);
        return project.getQueryResponse();
    }

    private LinkedHashSet<JSONObject> getRegionsOfProject(JSONObject projectQueryJson) {
        Long projectId = Long.valueOf(projectQueryJson.get("projectId").toString());
        String secret = projectQueryJson.get("secret").toString().toUpperCase();
        Set<Region> regions = apiService.queryForRegionsOfProject(projectId, secret);
        return regions.stream()
                      .sorted()
                      .map(Region::getQueryResponse)
                      .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private Scenario getScenario(JSONObject beaconQueryJson) {
        String uuid = beaconQueryJson.get("uuid").toString().toUpperCase();
        Integer major = Integer.valueOf(beaconQueryJson.get("major").toString());
        Integer minor = Integer.valueOf(beaconQueryJson.get("minor").toString());
        String secret = beaconQueryJson.get("secret").toString().toUpperCase();
        return scenarioService.queryForScenario(uuid, major, minor, secret);
    }

    private void verifyProjectQueryRequest(JSONObject projectQueryJson) {
        if (!projectQueryJson.containsKey("projectId") ||
                !projectQueryJson.containsKey("secret")) {
            throw new MalformedRequestException();
        }
    }

    private void verifyBeaconQueryRequest(JSONObject beaconQueryJson) {
        if (!beaconQueryJson.containsKey("uuid") ||
                !beaconQueryJson.containsKey("major") ||
                !beaconQueryJson.containsKey("minor") ||
                !beaconQueryJson.containsKey("secret")) {
            throw new MalformedRequestException();
        }
    }
}
