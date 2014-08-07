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
import java.util.Set;

public class ProjectBuilder {
    private Project project;

    public ProjectBuilder() {
        this.project = new Project();
    }

    public ProjectBuilder withProjectId(Long projectId) {
        this.project.setProjectId(projectId);
        return this;
    }

    public ProjectBuilder withName(String name) {
        this.project.setName(name);
        return this;
    }

    public ProjectBuilder withDescription(String description) {
        this.project.setDescription(description);
        return this;
    }

    public ProjectBuilder withCreationDate(Date creationDate) {
        this.project.setCreationDate(creationDate);
        return this;
    }

    public ProjectBuilder withBeacons(Set<Beacon> beacons) {
        this.project.setBeacons(beacons);
        return this;
    }

    public ProjectBuilder withBeaconGroups(Set<BeaconGroup> beaconGroups) {
        this.project.setBeaconGroups(beaconGroups);
        return this;
    }

    public ProjectBuilder withProjectSecret(String projectSecret) {
        this.project.setProjectSecret(projectSecret);
        return this;
    }

    public Project build() {
        return this.project;
    }
}
