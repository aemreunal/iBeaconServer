package com.aemreunal.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.aemreunal.config.GlobalSettings;
import com.aemreunal.controller.DeleteResponse;
import com.aemreunal.domain.Beacon;
import com.aemreunal.repository.beacon.BeaconRepository;
import com.aemreunal.repository.beacon.BeaconSpecifications;

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
    private BeaconRepository beaconRepository;

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

        return beaconRepository.save(beacon);
    }

    /**
     * Returns the list of all the beacons
     *
     * @return A list of all beacons
     */
    public List<Beacon> findAll() {
        if (GlobalSettings.DEBUGGING) {
            System.out.println("Finding all beacons");
        }

        List<Beacon> beaconList = new ArrayList<Beacon>();

        for (Beacon beacon : beaconRepository.findAll()) {
            beaconList.add(beacon);
        }

        return beaconList;
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

        return beaconRepository.findAll(BeaconSpecifications.beaconWithSpecification(projectId, uuid, major, minor));
    }

    /**
     * Finds the beacon with the given ID
     *
     * @param id
     *     The ID of the beacon to search for
     *
     * @return The beacon with that ID
     */
    public Beacon findById(Long id) {
        if (GlobalSettings.DEBUGGING) {
            System.out.println("Finding beacon with ID = \'" + id + "\'");
        }

        return beaconRepository.findOne(id);
    }

    /**
     * Deletes the beacon with the given ID
     *
     * @param id
     *     The ID of the beacon to delete
     *
     * @return Whether the beacon was deleted or not
     */
    public DeleteResponse delete(Long id) {
        if (GlobalSettings.DEBUGGING) {
            System.out.println("Deleting beacon with ID = \'" + id + "\'");
        }

        if (beaconRepository.exists(id)) {
            beaconRepository.delete(id);
            return DeleteResponse.DELETED;
        } else {
            return DeleteResponse.NOT_FOUND;
        }
    }

}
