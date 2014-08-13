package com.aemreunal.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.aemreunal.config.GlobalSettings;
import com.aemreunal.controller.DeleteResponse;
import com.aemreunal.domain.Beacon;
import com.aemreunal.domain.Project;
import com.aemreunal.repository.beacon.BeaconRepo;
import com.aemreunal.repository.beacon.BeaconSpecs;

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
public class BeaconService {
    @Autowired
    private BeaconRepo beaconRepo;

    /**
     * Saves/updates the given beacon
     *
     * @param beacon
     *     The beacon to save/update
     *
     * @return The saved/updated beacon
     */
    public Beacon save(Beacon beacon) {
        if (GlobalSettings.DEBUGGING) {
            System.out.println("Saving beacon with ID = \'" + beacon.getBeaconId() + "\'");
        }

        return beaconRepo.save(beacon);
    }

    /**
     * Finds beacons conforming to given specifications
     *
     * @param projectId
     *     The project ID constraint
     * @param uuid
     *     The UUID field constraint
     * @param major
     *     The Major field constraint
     * @param minor
     *     The Minor field constraint
     *
     * @return The list of beacons conforming to given constraints
     */
    public List<Beacon> findBeaconsBySpecs(Long projectId, String uuid, String major, String minor) {
        if (GlobalSettings.DEBUGGING) {
            System.out.println("Finding beacons with UUID = \'" + uuid + "\' major = \'" + major + "\' minor = \'" + minor + "\'");
        }

        return beaconRepo.findAll(BeaconSpecs.beaconWithSpecification(projectId, uuid, major, minor));
    }

    /**
     * Finds the beacon with the given ID in the given project
     *
     * @param beaconId
     *     The ID of the beacon to search for
     * @param project
     *     The project to search in
     *
     * @return The beacon with the given ID
     */
    public Beacon findByBeaconIdAndProject(Long beaconId, Project project) {
        if (GlobalSettings.DEBUGGING) {
            System.out.println("Finding beacon with ID = \'" + beaconId + "\' in project = \'" + project.getProjectId() + "\'");
        }

        return beaconRepo.findByBeaconIdAndProject(beaconId, project);
    }

    /**
     * Deletes the beacon with the given ID
     *
     * @param projectId
     *     The ID of the project to delete the beacon from
     * @param beaconId
     *     The ID of the beacon to delete
     *
     * @return Whether the beacon was deleted or not
     */
    public DeleteResponse delete(Long projectId, Long beaconId) {
        if (GlobalSettings.DEBUGGING) {
            System.out.println("Deleting beacon with ID = \'" + beaconId + "\'");
        }

        if (isMember(projectId, beaconId)) {
            if (GlobalSettings.DEBUGGING) {
                System.out.println("Project " + projectId + " has beacon " + beaconId + ", deleting.");
            }
            beaconRepo.delete(beaconId);
            return DeleteResponse.DELETED;
        } else {
            if (GlobalSettings.DEBUGGING) {
                System.out.println("Project " + projectId + " does not have beacon " + beaconId + ".");
            }
            return DeleteResponse.NOT_FOUND;
        }
    }

    /**
     * Checks whether the given beacon belongs to the given project
     *
     * @param projectId
     *     The ID of the project to check in
     * @param beaconId
     *     The ID of the beacon to check for
     *
     * @return Whether the beacon belongs to the project
     */
    public boolean isMember(Long projectId, Long beaconId) {
        List<Beacon> beacons = beaconRepo.findAll(BeaconSpecs.beaconExistsSpecification(projectId, beaconId));
        return beacons.size() >= 1;
    }
}
