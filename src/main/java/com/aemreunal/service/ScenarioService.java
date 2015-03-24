package com.aemreunal.service;

/*
 ***************************
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
 ***************************
 */

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.aemreunal.config.GlobalSettings;
import com.aemreunal.domain.Beacon;
import com.aemreunal.domain.Project;
import com.aemreunal.domain.Scenario;
import com.aemreunal.exception.scenario.BeaconDoesNotHaveScenarioException;
import com.aemreunal.exception.scenario.BeaconHasScenarioException;
import com.aemreunal.exception.scenario.NoScenarioForQueryException;
import com.aemreunal.exception.scenario.ScenarioNotFoundException;
import com.aemreunal.repository.scenario.ScenarioRepo;

@Transactional
@Service
public class ScenarioService {
    @Autowired
    private ProjectService projectService;

    @Autowired
    private BeaconService beaconService;

    @Autowired
    private ScenarioRepo scenarioRepo;

    public Scenario save(String username, Long projectId, Scenario scenario) throws ConstraintViolationException {
        if (GlobalSettings.DEBUGGING) {
            System.out.println("Saving scenario with ID = \'" + scenario.getScenarioId() + "\'");
        }
        // Even though the 'project' variable is only used inside the if-clause,
        // the Project is found no matter what to ensure it exists and legitimate.
        Project project = projectService.getProject(username, projectId);
        if (scenario.getProject() == null) {
            // This means it hasn't been saved yet
            scenario.setProject(project);
        }
        return scenarioRepo.save(scenario);
    }

    @Transactional(readOnly = true)
    public List<Scenario> getScenariosOfProject(String username, Long projectId) {
        Project project = projectService.getProject(username, projectId);
        return project.getScenarios().stream().collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Scenario getScenario(String username, Long projectId, Long scenarioId) throws ScenarioNotFoundException {
        if (GlobalSettings.DEBUGGING) {
            System.out.println("Finding scenario with ID = \'" + scenarioId + "\' in project = \'" + projectId + "\'");
        }
        Project project = projectService.getProject(username, projectId);
        Scenario scenario = scenarioRepo.findByScenarioIdAndProject(scenarioId, project);
        if (scenario == null) {
            throw new ScenarioNotFoundException(scenarioId);
        }
        return scenario;
    }

    @Transactional(readOnly = true)
    public Scenario queryForScenario(String uuid, Integer major, Integer minor, String projectSecret)
    throws NoScenarioForQueryException {
        Beacon beacon = beaconService.queryForBeacon(uuid, major, minor, projectSecret);
        Scenario scenario = beacon.getScenario();
        if (scenario == null) {
            throw new NoScenarioForQueryException(uuid, major, minor);
        }
        return scenario;
    }

    @Transactional(readOnly = true)
    public Set<Beacon> getBeaconsInScenario(String username, Long projectId, Long scenarioId) {
        Scenario scenario = this.getScenario(username, projectId, scenarioId);
        return scenario.getBeacons().stream().collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public Scenario delete(String username, Long projectId, Long scenarioId) {
        if (GlobalSettings.DEBUGGING) {
            System.out.println("Deleting scenario with ID = \'" + scenarioId + "\'");
        }
        Scenario scenario = this.getScenario(username, projectId, scenarioId);
        removeAllBeaconsFromScenario(username, projectId, scenario);
        scenarioRepo.delete(scenario);
        return scenario;
    }

    private void removeAllBeaconsFromScenario(String username, Long projectId, Scenario scenario) {
        for (Beacon beacon : scenario.getBeacons()) {
            removeBeaconFromScenario(username, projectId, scenario.getScenarioId(), beacon);
        }
    }

    public Beacon addBeaconToScenario(String username, Long projectId, Long scenarioId, Long beaconId)
    throws BeaconHasScenarioException {
        Beacon beacon = beaconService.getBeacon(username, projectId, scenarioId, beaconId);
        return addBeaconToScenario(username, projectId, scenarioId, beacon);
    }

    private Beacon addBeaconToScenario(String username, Long projectId, Long scenarioId, Beacon beacon) {
        if (beacon.getScenario() != null) {
            throw new BeaconHasScenarioException(beacon.getBeaconId(), beacon.getScenario().getScenarioId());
        }
        Scenario scenario = getScenario(username, projectId, scenarioId);
        beaconService.setBeaconScenario(username, projectId, beacon.getRegion().getRegionId(), beacon, scenario);
        return beacon;
    }

    public Beacon removeBeaconFromScenario(String username, Long projectId, Long scenarioId, Long beaconId)
    throws BeaconDoesNotHaveScenarioException, BeaconHasScenarioException {
        Beacon beacon = beaconService.getBeacon(username, projectId, scenarioId, beaconId);
        removeBeaconFromScenario(username, projectId, scenarioId, beacon);
        return beacon;
    }

    public Beacon removeBeaconFromScenario(String username, Long projectId, Long scenarioId, Beacon beacon) {
        if (beacon.getScenario() == null) {
            throw new BeaconDoesNotHaveScenarioException(beacon.getBeaconId(), scenarioId);
        } else if (!(beacon.getScenario().getScenarioId().equals(scenarioId))) {
            throw new BeaconHasScenarioException(beacon.getBeaconId(), beacon.getScenario().getScenarioId());
        }
        beaconService.setBeaconScenario(username, projectId, beacon.getRegion().getRegionId(), beacon, null);
        return beacon;
    }
}
