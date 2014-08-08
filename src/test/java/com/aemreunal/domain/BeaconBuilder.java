package com.aemreunal.domain;

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

import java.util.Date;
import javax.json.JsonObject;

public class BeaconBuilder {
    private Beacon beacon;

    public BeaconBuilder() {
        this.beacon = new Beacon();
    }

    public BeaconBuilder withBeaconId(Long beaconId) {
        this.beacon.setBeaconId(beaconId);
        return this;
    }

    public BeaconBuilder withUuid(String uuid) {
        this.beacon.setUuid(uuid.toUpperCase());
        return this;
    }

    public BeaconBuilder withMajor(String major) {
        this.beacon.setMajor(major.toUpperCase());
        return this;
    }

    public BeaconBuilder withMinor(String minor) {
        this.beacon.setMinor(minor.toUpperCase());
        return this;
    }

    public BeaconBuilder withDescription(String description) {
        this.beacon.setDescription(description);
        return this;
    }

    public BeaconBuilder withGroup(BeaconGroup group) {
        this.beacon.setGroup(group);
        return this;
    }

    public BeaconBuilder withProject(Project project) {
        this.beacon.setProject(project);
        return this;
    }

    public BeaconBuilder withCreationDate(Date creationDate) {
        this.beacon.setCreationDate(creationDate);
        return this;
    }

    public BeaconBuilder withJson(JsonObject beaconJson) {
        this.beacon.setUuid(beaconJson.getString("uuid"));
        this.beacon.setMajor(beaconJson.getString("major"));
        this.beacon.setMinor(beaconJson.getString("minor"));
        this.beacon.setDescription(beaconJson.getString("description"));
        this.beacon.setCreationDate(new Date());
        return this;
    }

    public Beacon build() {
        return this.beacon;
    }
}
