package com.aemreunal.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.aemreunal.config.GlobalSettings;
import com.aemreunal.controller.DeleteResponse;
import com.aemreunal.domain.Beacon;
import com.aemreunal.domain.BeaconGroup;
import com.aemreunal.domain.Project;
import com.aemreunal.exception.beaconGroup.BeaconGroupNotFoundException;
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

    /**
     * Saves/updates the given beacon group
     *
     * @param beaconGroup
     *     The beacon group to save/update
     *
     * @return The saved/updated beacon group
     */
    public BeaconGroup save(BeaconGroup beaconGroup) {
        if (GlobalSettings.DEBUGGING) {
            System.out.println("Saving beacon group with ID = \'" + beaconGroup.getBeaconGroupId() + "\'");
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

    public BeaconGroup findByBeaconGroupIdAndProject(String username, Long projectId, Long beaconGroupId) {
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
        BeaconGroup beaconGroup = this.findByBeaconGroupIdAndProject(username, projectId, beaconGroupId);
        List<Beacon> beaconList = new ArrayList<Beacon>();
        for (Beacon beacon : beaconGroup.getBeacons()) {
            beaconList.add(beacon);
        }
        return beaconList;
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
        BeaconGroup beaconGroup = this.findByBeaconGroupIdAndProject(username, projectId, beaconGroupId);
        updateBeaconsInGroup(beaconGroup, username, projectId);
        beaconGroupRepo.delete(beaconGroup);
        return beaconGroup;
    }

    /**
     * Checks whether the given beacon group belongs to the given project
     *
     * @param projectId
     *     The ID of the project to check in
     * @param beaconGroupId
     *     The ID of the beacon group to check for
     *
     * @return Whether the beacon group belongs to the project
     */
    public boolean isMember(Long projectId, Long beaconGroupId) {
        List<BeaconGroup> beaconGroups = beaconGroupRepo.findAll(BeaconGroupSpecs.beaconGroupExistsSpecification(projectId, beaconGroupId));
        return beaconGroups.size() >= 1;
    }

    private void updateBeaconsInGroup(BeaconGroup beaconGroup, String username, Long projectId) {
        for (Beacon beacon : beaconGroup.getBeacons()) {
            beacon.setGroup(null);
            beaconService.save(username, projectId, beacon);
        }
    }
}
