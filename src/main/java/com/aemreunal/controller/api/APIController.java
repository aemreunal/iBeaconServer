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
     *      uuid:   <beacon UUID>
     *      major:  <beacon Major>
     *      minor:  <beacon Minor>
     *      secret: <project Secret>
     * }
     */
    @RequestMapping(method = RequestMethod.GET, value = GlobalSettings.API_BEACON_QUERY_PATH_MAPPING, produces = "application/json;charset=UTF-8")
    public ResponseEntity<Scenario> queryForScenario(@RequestBody JSONObject beaconQueryJson) {
        String uuid = (String) beaconQueryJson.get("uuid");
        String major = (String) beaconQueryJson.get("major");
        String minor = (String) beaconQueryJson.get("minor");
        String secret = (String) beaconQueryJson.get("secret");
        Scenario scenario = scenarioService.queryForScenario(uuid, major, minor, secret);
        // TODO do something intelligent instead of just returning the scenario
        return new ResponseEntity<Scenario>(scenario, HttpStatus.OK);
    }
}
