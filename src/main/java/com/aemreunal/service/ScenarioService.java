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
import java.util.List;
import javax.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.aemreunal.config.GlobalSettings;
import com.aemreunal.domain.Beacon;
import com.aemreunal.domain.BeaconGroup;
import com.aemreunal.domain.Project;
import com.aemreunal.domain.Scenario;
import com.aemreunal.exception.scenario.ScenarioNotFoundException;
import com.aemreunal.repository.scenario.ScenarioRepo;

@Transactional
@Service
public class ScenarioService {
    @Autowired
    private ProjectService projectService;

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
        if(scenario == null) {
            throw new ScenarioNotFoundException(scenarioId);
        }
        return scenario;
    }

    public List<Beacon> getBeaconsInScenario(String username, Long projectId, Long scenarioId) {
        Scenario scenario = this.getScenario(username, projectId, scenarioId);
        List<Beacon> beacons = new ArrayList<Beacon>();
        for (Beacon beacon : scenario.getBeacons()) {
            beacons.add(beacon);
        }
        return beacons;
    }

    public List<BeaconGroup> getBeaconGroupsInScenario(String username, Long projectId, Long scenarioId) {
        Scenario scenario = this.getScenario(username, projectId, scenarioId);
        List<BeaconGroup> beaconGroups = new ArrayList<BeaconGroup>();
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
}
