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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.aemreunal.config.GlobalSettings;
import com.aemreunal.domain.*;
import com.aemreunal.exception.MalformedRequestException;
import com.aemreunal.exception.connection.ConnectionNotFoundException;
import com.aemreunal.exception.imageStorage.ImageLoadException;
import com.aemreunal.exception.textStorage.TextLoadException;
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
     * Project ID JSON structure:
     * {
     *      "projectId": <project ID>,
     *      "secret":    <project Secret>
     * }
     */
    @RequestMapping(method = RequestMethod.POST, value = GlobalSettings.API_PROJECT_QUERY_PATH_MAPPING, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JSONObject> queryForProject(@RequestBody JSONObject idJson) {
        verifyProjectQueryRequest(idJson);
        JSONObject project = getProject(idJson);
        return new ResponseEntity<JSONObject>(project, HttpStatus.OK);
    }


    @RequestMapping(method = RequestMethod.POST, value = GlobalSettings.API_REGION_QUERY_PATH_MAPPING, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LinkedHashSet<JSONObject>> queryForRegions(@RequestBody JSONObject idJson) {
        verifyProjectQueryRequest(idJson);
        LinkedHashSet<JSONObject> regions = getRegionsOfProject(idJson);
        return new ResponseEntity<LinkedHashSet<JSONObject>>(regions, HttpStatus.OK);
    }


    @RequestMapping(method = RequestMethod.POST, value = GlobalSettings.API_CONNECTION_QUERY_PATH_MAPPING, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LinkedHashSet<JSONObject>> queryForConnections(@RequestBody JSONObject idJson) {
        verifyProjectQueryRequest(idJson);
        LinkedHashSet<JSONObject> regions = getConnectionsOfProject(idJson);
        return new ResponseEntity<LinkedHashSet<JSONObject>>(regions, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, value = GlobalSettings.API_BEACON_QUERY_PATH_MAPPING, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LinkedHashSet<JSONObject>> queryForBeacons(@RequestBody JSONObject idJson,
                                                                     @PathVariable Long regionId) {
        verifyProjectQueryRequest(idJson);
        LinkedHashSet<JSONObject> beacons = getBeaconsOfRegion(idJson, regionId);
        return new ResponseEntity<LinkedHashSet<JSONObject>>(beacons, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, value = GlobalSettings.API_REGION_IMG_QUERY_PATH_MAPPING, produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> queryForRegionImage(@RequestBody JSONObject idJson,
                                                      @PathVariable Long regionId)
    throws ImageLoadException {
        verifyProjectQueryRequest(idJson);
        byte[] regionImage = getRegionImage(idJson, regionId);
        if (regionImage == null) {
            return new ResponseEntity<byte[]>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<byte[]>(regionImage, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, value = GlobalSettings.API_CONNECTION_IMG_QUERY_PATH_MAPPING, produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> queryForConnectionImage(@RequestBody JSONObject idJson,
                                                          @PathVariable Long connectionId)
    throws ImageLoadException, ConnectionNotFoundException {
        verifyProjectQueryRequest(idJson);
        byte[] connectionImage = getConnectionImage(idJson, connectionId);
        if (connectionImage == null) {
            return new ResponseEntity<byte[]>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<byte[]>(connectionImage, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, value = GlobalSettings.API_BEACON_INFO_QUERY_PATH_MAPPING, produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> queryForBeaconLocationInfo(@RequestBody JSONObject idJson,
                                                             @PathVariable Long regionId,
                                                             @PathVariable Long beaconId) {
        verifyProjectQueryRequest(idJson);
        String locationInfo = getBeaconLocationInfo(idJson, regionId, beaconId);
        if (locationInfo == null) {
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<String>(locationInfo, HttpStatus.OK);
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
//    @RequestMapping(method = RequestMethod.POST, value = GlobalSettings.API_BEACON_QUERY_PATH_MAPPING, produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<JSONObject> queryForScenario(@RequestBody JSONObject beaconQueryJson) {
//        verifyBeaconQueryRequest(beaconQueryJson);
//        Scenario scenario = getScenario(beaconQueryJson);
//        return new ResponseEntity<JSONObject>(scenario.generateQueryResponse(), HttpStatus.OK);
//    }

    private JSONObject getProject(JSONObject idJson) {
        Long projectId = Long.valueOf(idJson.get("projectId").toString());
        String secret = idJson.get("secret").toString().toUpperCase();
        Project project = apiService.queryForProject(projectId, secret);
        return project.getQueryResponse();
    }

    private LinkedHashSet<JSONObject> getRegionsOfProject(JSONObject idJson) {
        Long projectId = Long.valueOf(idJson.get("projectId").toString());
        String secret = idJson.get("secret").toString().toUpperCase();
        Set<Region> regions = apiService.queryForRegionsOfProject(projectId, secret);
        return regions.stream()
                      .sorted()
                      .map(Region::getQueryResponse)
                      .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private LinkedHashSet<JSONObject> getBeaconsOfRegion(JSONObject idJson, Long regionId) {
        Long projectId = Long.valueOf(idJson.get("projectId").toString());
        String secret = idJson.get("secret").toString().toUpperCase();

        // Getting LazyInitException
//        Region region = apiService.queryForRegionOfProject(projectId, secret, regionId);

//        Set<Region> regions = apiService.queryForRegionsOfProject(projectId, secret);
//        Region region = regions.stream()
//                               .filter(regionVar -> regionVar.getRegionId().equals(regionId))
//                               .findFirst()
//                               .get();
        return apiService.queryForBeaconsOfRegion(projectId, secret, regionId)
                         .stream()
                         .sorted()
                         .map(Beacon::getQueryResponse)
                         .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private LinkedHashSet<JSONObject> getConnectionsOfProject(JSONObject idJson) {
        Long projectId = Long.valueOf(idJson.get("projectId").toString());
        String secret = idJson.get("secret").toString().toUpperCase();
        Set<Connection> connections = apiService.queryForConnections(projectId, secret);
        return connections.stream()
                          .map(Connection::getQueryResponse)
                          .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private byte[] getRegionImage(JSONObject idJson, Long regionId)
    throws ImageLoadException {
        Long projectId = Long.valueOf(idJson.get("projectId").toString());
        String secret = idJson.get("secret").toString().toUpperCase();
        return apiService.queryForImageOfRegion(projectId, secret, regionId);
    }

    private byte[] getConnectionImage(JSONObject idJson, Long connectionId)
    throws ImageLoadException, ConnectionNotFoundException {
        Long projectId = Long.valueOf(idJson.get("projectId").toString());
        String secret = idJson.get("secret").toString().toUpperCase();
        return apiService.queryForImageOfConnection(projectId, secret, connectionId);
    }

    private String getBeaconLocationInfo(JSONObject idJson, Long regionId, Long beaconId) {
        Long projectId = Long.valueOf(idJson.get("projectId").toString());
        String secret = idJson.get("secret").toString().toUpperCase();
        try {
            return apiService.queryForLocationInfoOfBeacon(projectId, secret, regionId, beaconId);
        } catch (TextLoadException e) {
            // Text can't be loaded or there is no text
            return null;
        }
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
