package com.aemreunal.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.aemreunal.config.GlobalSettings;
import com.aemreunal.domain.Beacon;
import com.aemreunal.domain.BeaconGroup;
import com.aemreunal.domain.Project;
import com.aemreunal.domain.Scenario;
import com.aemreunal.exception.beaconGroup.BeaconDoesntHaveGroupException;
import com.aemreunal.exception.beaconGroup.BeaconGroupNotFoundException;
import com.aemreunal.exception.beaconGroup.BeaconHasGroupException;
import com.aemreunal.repository.beaconGroup.BeaconGroupRepo;
import com.aemreunal.repository.beaconGroup.BeaconGroupSpecs;

/*
 **************************
 * Copyright (c) 2014     *
 *                        *
 * This code belongs to:  *
 *                        *
 * Ahmet Emre Ünal        *
 * S001974                *
 *                        *
 * aemreunal@gmail.com    *
 * emre.unal@ozu.edu.tr   *
 *                        *
 * aemreunal.com          *
 **************************
 */

@Transactional
@Service
public class BeaconGroupService {

    @Autowired
    private BeaconGroupRepo beaconGroupRepo;

    @Autowired
    private BeaconService beaconService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ScenarioService scenarioService;

    /**
     * Saves/updates the given beacon group
     *
     * @param beaconGroup
     *     The beacon group to save/update
     *
     * @return The saved/updated beacon group
     */
    public BeaconGroup save(String username, Long projectId, BeaconGroup beaconGroup) {
        if (GlobalSettings.DEBUGGING) {
            System.out.println("Saving beacon group with ID = \'" + beaconGroup.getBeaconGroupId() + "\'");
        }
        Project project = projectService.findProjectById(username, projectId);
        if (beaconGroup.getProject() == null) {
            beaconGroup.setProject(project);
        }
        return beaconGroupRepo.save(beaconGroup);
    }

    public List<BeaconGroup> getAllBeaconGroupsOf(String username, Long projectId) {
        Project project = projectService.findProjectById(username, projectId);
        List<BeaconGroup> beaconGroups = new ArrayList<BeaconGroup>();
        for (BeaconGroup beaconGroup : project.getBeaconGroups()) {
            beaconGroups.add(beaconGroup);
        }
        return beaconGroups;
    }

    public BeaconGroup getBeaconGroup(String username, Long projectId, Long beaconGroupId) {
        if (GlobalSettings.DEBUGGING) {
            System.out.println("Finding beacon group with ID = \'" + beaconGroupId + "\'");
        }
        Project project = projectService.findProjectById(username, projectId);
        BeaconGroup beaconGroup = beaconGroupRepo.findByBeaconGroupIdAndProject(beaconGroupId, project);
        if (beaconGroup == null) {
            throw new BeaconGroupNotFoundException(beaconGroupId);
        }
        return beaconGroup;
    }

    /**
     * Finds the beacon groups conforming to given specifications
     *
     * @param projectId
     *     The project ID constraint
     * @param beaconGroupName
     *     The name field constraint
     *
     * @return The list of beacon groups conforming to given constraints
     */
    public List<BeaconGroup> findBeaconGroupsBySpecs(String username, Long projectId, String beaconGroupName) {
        if (GlobalSettings.DEBUGGING) {
            System.out.println("Finding beacon groups with projectID = \'" + projectId + "\' and name =\'" + beaconGroupName + "\'");
        }
        Project project = projectService.findProjectById(username, projectId);
        List<BeaconGroup> beaconGroups = beaconGroupRepo.findAll(BeaconGroupSpecs.beaconGroupWithSpecification(project.getProjectId(), beaconGroupName));
        if (beaconGroups.size() == 0) {
            throw new BeaconGroupNotFoundException();
        }
        return beaconGroups;
    }

    public List<Beacon> getMembersOfBeaconGroup(String username, Long projectId, Long beaconGroupId) {
        BeaconGroup beaconGroup = this.getBeaconGroup(username, projectId, beaconGroupId);
        List<Beacon> beaconList = new ArrayList<Beacon>();
        for (Beacon beacon : beaconGroup.getBeacons()) {
            beaconList.add(beacon);
        }
        return beaconList;
    }

    public BeaconGroup addBeaconToGroup(String username, Long projectId, Long beaconGroupId, Long beaconId) {
        BeaconGroup beaconGroup = getBeaconGroup(username, projectId, beaconGroupId);
        Beacon beacon = beaconService.getBeacon(username, projectId, beaconId);
        // Check pre-existing group
        BeaconGroup currentGroup = beacon.getGroup();
        if (currentGroup != null) {
            throw new BeaconHasGroupException(beaconId, currentGroup.getBeaconGroupId());
        }
        // Check pre-existing scenario
        Scenario currentScenario = beacon.getScenario();
        if (currentScenario != null) {
            scenarioService.removeBeaconFromScenario(username, projectId, currentScenario.getScenarioId(), beaconId);
        }
        beacon.setGroup(beaconGroup);
        beaconService.save(username, projectId, beacon);
        return beaconGroup;
    }

    public BeaconGroup removeBeaconFromGroup(String username, Long projectId, Long beaconGroupId, Long beaconId) {
        BeaconGroup beaconGroup = getBeaconGroup(username, projectId, beaconGroupId);
        Beacon beacon = beaconService.getBeacon(username, projectId, beaconId);
        if (beacon.getGroup() == null) {
            throw new BeaconDoesntHaveGroupException(beacon.getBeaconId(), beaconGroupId);
        } else if(!(beacon.getGroup().getBeaconGroupId().equals(beaconGroupId))) {
            throw new BeaconHasGroupException(beaconId, beacon.getGroup().getBeaconGroupId());
        }
        beacon.setGroup(null);
        beaconService.save(username, projectId, beacon);
        return beaconGroup;
    }

    /**
     * Deletes the beacon group with the given ID and updates the beacons in the group.
     *
     * @param projectId
     *     The ID of the project to delete the beacon from
     * @param beaconGroupId
     *     The ID of the beacon group to delete
     *
     * @return Whether the beacon group was deleted or not
     */
    public BeaconGroup delete(String username, Long projectId, Long beaconGroupId) {
        if (GlobalSettings.DEBUGGING) {
            System.out.println("Deleting beacon group with ID = \'" + beaconGroupId + "\'");
        }
        BeaconGroup beaconGroup = this.getBeaconGroup(username, projectId, beaconGroupId);
        updateBeaconsInGroup(beaconGroup, username, projectId);
        beaconGroupRepo.delete(beaconGroup);
        return beaconGroup;
    }

    private void updateBeaconsInGroup(BeaconGroup beaconGroup, String username, Long projectId) {
        for (Beacon beacon : beaconGroup.getBeacons()) {
            beacon.setGroup(null);
            beaconService.save(username, projectId, beacon);
        }
    }
}
