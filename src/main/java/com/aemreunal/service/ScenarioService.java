package com.aemreunal.service;

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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.aemreunal.config.GlobalSettings;
import com.aemreunal.domain.Beacon;
import com.aemreunal.domain.BeaconGroup;
import com.aemreunal.domain.Project;
import com.aemreunal.domain.Scenario;
import com.aemreunal.exception.scenario.*;
import com.aemreunal.repository.scenario.ScenarioRepo;

@Transactional
@Service
public class ScenarioService {
    @Autowired
    private ProjectService projectService;

    @Autowired
    private BeaconService beaconService;

    @Autowired
    private BeaconGroupService beaconGroupService;

    @Autowired
    private ScenarioRepo scenarioRepo;

    public Scenario save(String username, Long projectId, Scenario scenario) throws ConstraintViolationException {
        if (GlobalSettings.DEBUGGING) {
            System.out.println("Saving scenario with ID = \'" + scenario.getScenarioId() + "\'");
        }
        // Even though the 'project' variable is only used inside the if-clause,
        // the Project is found no matter what to ensure it exists and legitimate.
        Project project = projectService.findProjectById(username, projectId);
        if (scenario.getProject() == null) {
            // This means it hasn't been saved yet
            scenario.setProject(project);
        }
        return scenarioRepo.save(scenario);
    }

    public List<Scenario> getScenariosOfProject(String username, Long projectId) {
        Project project = projectService.findProjectById(username, projectId);
        List<Scenario> scenarios = new ArrayList<Scenario>();
        for (Scenario scenario : project.getScenarios()) {
            scenarios.add(scenario);
        }
        return scenarios;
    }

    public Scenario getScenario(String username, Long projectId, Long scenarioId) throws ScenarioNotFoundException {
        if (GlobalSettings.DEBUGGING) {
            System.out.println("Finding scenario with ID = \'" + scenarioId + "\' in project = \'" + projectId + "\'");
        }
        Project project = projectService.findProjectById(username, projectId);
        Scenario scenario = scenarioRepo.findByScenarioIdAndProject(scenarioId, project);
        if (scenario == null) {
            throw new ScenarioNotFoundException(scenarioId);
        }
        return scenario;
    }

    public Scenario queryForScenario(String uuid,
                                     String major,
                                     String minor,
                                     String projectSecret)
    throws NoScenarioForQueryException {
        Beacon beacon = beaconService.queryForBeacon(uuid, major, minor, projectSecret);
        Scenario scenario;
        if (beacon.getGroup() != null) {
            scenario = beacon.getGroup().getScenario();
        } else {
            scenario = beacon.getScenario();
        }
        if (scenario == null) {
            throw new NoScenarioForQueryException(uuid, major, minor);
        }
        return scenario;
    }

    public Set<Beacon> getBeaconsInScenario(String username, Long projectId, Long scenarioId) {
        Scenario scenario = this.getScenario(username, projectId, scenarioId);
        Set<Beacon> beacons = new LinkedHashSet<>();
        for (Beacon beacon : scenario.getBeacons()) {
            beacons.add(beacon);
        }
        return beacons;
    }

    public Set<BeaconGroup> getBeaconGroupsInScenario(String username, Long projectId, Long scenarioId) {
        Scenario scenario = this.getScenario(username, projectId, scenarioId);
        Set<BeaconGroup> beaconGroups = new LinkedHashSet<>();
        for (BeaconGroup beaconGroup : scenario.getBeaconGroups()) {
            beaconGroups.add(beaconGroup);
        }
        return beaconGroups;
    }

    public Scenario delete(String username, Long projectId, Long scenarioId) {
        if (GlobalSettings.DEBUGGING) {
            System.out.println("Deleting scenario with ID = \'" + scenarioId + "\'");
        }
        Scenario scenario = this.getScenario(username, projectId, scenarioId);
        scenarioRepo.delete(scenario);
        return scenario;
    }

    public Beacon addBeaconToScenario(String username, Long projectId, Long scenarioId, Long beaconId)
    throws BeaconHasScenarioException, BeaconWithGroupScenarioException {
        Beacon beacon = beaconService.getBeacon(username, projectId, beaconId);
        Scenario scenario = getScenario(username, projectId, scenarioId);
        if (beacon.getGroup() != null) {
            throw new BeaconWithGroupScenarioException(beaconId, beacon.getGroup().getBeaconGroupId());
        }
        if (beacon.getScenario() != null) {
            throw new BeaconHasScenarioException(beaconId, beacon.getScenario().getScenarioId());
        }
        beacon.setScenario(scenario);
        beaconService.save(username, projectId, beacon);
        return beacon;
    }

    public Beacon removeBeaconFromScenario(String username, Long projectId, Long scenarioId, Long beaconId)
    throws BeaconDoesntHaveScenarioException, BeaconHasScenarioException, BeaconWithGroupScenarioException {
        Beacon beacon = beaconService.getBeacon(username, projectId, beaconId);
        if (beacon.getGroup() != null) {
            throw new BeaconWithGroupScenarioException(beaconId, beacon.getGroup().getBeaconGroupId());
        }
        if (beacon.getScenario() == null) {
            throw new BeaconDoesntHaveScenarioException(beaconId, scenarioId);
        } else if (!(beacon.getScenario().getScenarioId().equals(scenarioId))) {
            throw new BeaconHasScenarioException(beaconId, beacon.getScenario().getScenarioId());
        }
        beacon.setScenario(null);
        beaconService.save(username, projectId, beacon);
        return beacon;
    }

    public BeaconGroup addBeaconGroupToScenario(String username, Long projectId, Long scenarioId, Long beaconGroupId)
    throws BeaconGroupHasScenarioException {
        BeaconGroup beaconGroup = beaconGroupService.getBeaconGroup(username, projectId, beaconGroupId);
        Scenario scenario = getScenario(username, projectId, scenarioId);
        if (beaconGroup.getScenario() != null) {
            throw new BeaconGroupHasScenarioException(beaconGroupId, beaconGroup.getScenario().getScenarioId());
        }
        beaconGroup.setScenario(scenario);
        beaconGroupService.save(username, projectId, beaconGroup);
        return beaconGroup;
    }

    public BeaconGroup removeBeaconGroupFromScenario(String username, Long projectId, Long scenarioId, Long beaconGroupId)
    throws BeaconGroupHasScenarioException, BeaconGroupDoesntHaveScenarioException {
        BeaconGroup beaconGroup = beaconGroupService.getBeaconGroup(username, projectId, beaconGroupId);
        if (beaconGroup.getScenario() == null) {
            throw new BeaconGroupDoesntHaveScenarioException(beaconGroupId, scenarioId);
        } else if (!(beaconGroup.getScenario().getScenarioId().equals(scenarioId))) {
            throw new BeaconGroupHasScenarioException(beaconGroupId, beaconGroup.getScenario().getScenarioId());
        }
        beaconGroup.setScenario(null);
        beaconGroupService.save(username, projectId, beaconGroup);
        return beaconGroup;
    }
}
