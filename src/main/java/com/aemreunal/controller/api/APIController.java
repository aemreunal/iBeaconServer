package com.aemreunal.controller.api;

import net.minidev.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.aemreunal.config.GlobalSettings;
import com.aemreunal.domain.Scenario;
import com.aemreunal.exception.MalformedRequestException;
import com.aemreunal.service.ScenarioService;

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

@Controller
@RequestMapping(GlobalSettings.API_PATH_MAPPING)
public class APIController {
    @Autowired
    private ScenarioService scenarioService;

    /*
     * Beacon query JSON example:
     * {
     *      "uuid":   <beacon UUID>
     *      "major":  <beacon Major>
     *      "minor":  <beacon Minor>
     *      "secret": <project Secret>
     * }
     */
    @RequestMapping(method = RequestMethod.POST, value = GlobalSettings.API_BEACON_QUERY_PATH_MAPPING, produces = "application/json; charset=UTF-8")
    public ResponseEntity<JSONObject> queryForScenario(@RequestBody JSONObject beaconQueryJson) {
        verifyQueryRequest(beaconQueryJson);
        Scenario scenario = getScenario(beaconQueryJson);
        return new ResponseEntity<JSONObject>(scenario.generateQueryResponse(), HttpStatus.OK);
    }

    private void verifyQueryRequest(JSONObject beaconQueryJson) {
        if (!beaconQueryJson.containsKey("uuid") ||
            !beaconQueryJson.containsKey("major") ||
            !beaconQueryJson.containsKey("minor") ||
            !beaconQueryJson.containsKey("secret")) {
            throw new MalformedRequestException();
        }
    }

    private Scenario getScenario(JSONObject beaconQueryJson) {
        String uuid = beaconQueryJson.get("uuid").toString().toUpperCase();
        String major = beaconQueryJson.get("major").toString().toUpperCase();
        String minor = beaconQueryJson.get("minor").toString().toUpperCase();
        String secret = beaconQueryJson.get("secret").toString().toUpperCase();
        return scenarioService.queryForScenario(uuid, major, minor, secret);
    }
}
