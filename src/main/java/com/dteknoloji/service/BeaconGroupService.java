package com.dteknoloji.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.dteknoloji.config.GlobalSettings;
import com.dteknoloji.controller.DeleteResponse;
import com.dteknoloji.domain.Beacon;
import com.dteknoloji.domain.BeaconGroup;
import com.dteknoloji.repository.beaconGroup.BeaconGroupRepository;

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
    private BeaconGroupRepository repository;

    @Autowired
    private BeaconService beaconService;

    /**
     * Saves/updates the given beacon group
     *
     * @param beaconGroup
     *         The beacon group to save/update
     *
     * @return The saved/updated beacon group
     */
    public BeaconGroup save(BeaconGroup beaconGroup) {
        if (GlobalSettings.DEBUGGING) {
            System.out.println("Saving beacon group with ID = \'" + beaconGroup.getBeaconGroupId() + "\'");
        }

        return repository.save(beaconGroup);
    }

    /**
     * Returns the list of all the beacon groups
     *
     * @return A list of all beacon groups
     */
    public List<BeaconGroup> findAll() {
        if (GlobalSettings.DEBUGGING) {
            System.out.println("Finding all beacon groups");
        }

        List<BeaconGroup> beaconGroupList = new ArrayList<BeaconGroup>();

        for (BeaconGroup beaconGroup : repository.findAll()) {
            beaconGroupList.add(beaconGroup);
        }

        return beaconGroupList;
    }

    /**
     * Finds the beacon group with the given ID
     *
     * @param id
     *         The ID of the beacon group to search for
     *
     * @return The beacon group with that ID
     */
    public BeaconGroup findById(Long id) {
        if (GlobalSettings.DEBUGGING) {
            System.out.println("Finding beacon group with ID = \'" + id + "\'");
        }
        return repository.findOne(id);
    }

    /**
     * Deletes the beacon group with the given ID and updates the beacons in the group.
     *
     * @param id
     *         The ID of the beacon group to delete
     *
     * @return Whether the beacon group was deleted or not
     */
    public DeleteResponse delete(Long id) {
        if (GlobalSettings.DEBUGGING) {
            System.out.println("Deleting beacon group with ID = \'" + id + "\'");
        }
        if (repository.exists(id)) {
            updateBeaconsInGroup(id);
            repository.delete(id);
            return DeleteResponse.DELETED;
        } else {
            return DeleteResponse.NOT_FOUND;
        }
    }

    /**
     * Removes the association between a group and the beacons in the group
     *
     * @param id
     *         The ID of the group
     */
    private void updateBeaconsInGroup(Long id) {
        for (Beacon beacon : repository.findOne(id).getBeacons()) {
            beacon.setGroup(null);
            beaconService.save(beacon);
        }
    }
}
