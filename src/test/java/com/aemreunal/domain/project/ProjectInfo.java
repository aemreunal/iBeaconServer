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
import java.util.List;
import com.jayway.restassured.path.json.JsonPath;

public class ProjectInfo {
    public String     ownerUsername = "";
    public Long       projectId     = (long) -1;
    public String     secret        = "";
    public String     name          = "";
    public String     description   = "";
    public List<Long> beacons       = new ArrayList<>();
    public List<Long> beaconGroups  = new ArrayList<>();
    public List<Long> scenarios     = new ArrayList<>();

    public ProjectInfo(String ownerUsername, Long projectId, String secret, String name, String description) {
        this.ownerUsername = ownerUsername;
        this.projectId = projectId;
        this.secret = secret;
        this.name = name;
        this.description = description;
    }

    public ProjectInfo(JsonPath projectJson, String ownerUsername, String secret) {
        this.ownerUsername = ownerUsername;
        this.projectId = projectJson.getLong("projectId");
        this.secret = secret;
        this.name = projectJson.getString("name");
        this.description = projectJson.getString("description");

    }

    public ProjectInfo(HashMap<String, Object> projectMap, String ownerUsername, String secret) {
        this.ownerUsername = ownerUsername;
        this.projectId = Long.parseLong(projectMap.get("projectId").toString());
        this.secret = secret;
        this.name = projectMap.get("name").toString();
        this.description = projectMap.get("description").toString();

    }

    public void addBeacon(Long beaconId) {
        beacons.add(beaconId);
    }

    public void removeBeacon(Long beaconId) {
        beacons.remove(beaconId);
    }

    public void addBeaconGroup(Long beaconGroupId) {
        beacons.add(beaconGroupId);
    }

    public void removeBeaconGroup(Long beaconGroupId) {
        beacons.remove(beaconGroupId);
    }

    public void addScenario(Long scenarioId) {
        beacons.add(scenarioId);
    }

    public void removeScenario(Long scenarioId) {
        beacons.remove(scenarioId);
    }

    @Override
    public String toString() {
        String info = "Project Info:\n";
        info += "\tOwner: \'" + ownerUsername + "\'\n";
        info += "\tProject ID: \'" + projectId + "\'\n";
        info += "\tSecret: \'" + secret + "\'\n";
        info += "\tName: \'" + name + "\'\n";
        info += "\tDescription: \'" + description + "\'\n";
        info += "\tBeacons: " + beacons + "\n";
        info += "\tBeacon Groups: " + beaconGroups + "\n";
        info += "\tScenarios: " + scenarios + "\n";
        return info;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ProjectInfo)) {
            return false;
        } else {
            ProjectInfo otherProjectInfo = (ProjectInfo) obj;
            boolean ownerMatches = this.ownerUsername.equals(otherProjectInfo.ownerUsername);
            boolean idMatches = this.projectId.equals(otherProjectInfo.projectId);
            boolean nameMatches = this.name.equals(otherProjectInfo.name);
            boolean descriptionMatches = this.description.equals(otherProjectInfo.description);
            return ownerMatches && idMatches && nameMatches && descriptionMatches;
        }
    }
}
