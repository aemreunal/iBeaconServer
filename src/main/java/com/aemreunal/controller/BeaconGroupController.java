package com.aemreunal.controller;

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

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import com.aemreunal.domain.Beacon;
import com.aemreunal.domain.BeaconGroup;
import com.aemreunal.service.BeaconGroupService;
import com.aemreunal.service.BeaconService;

@Controller
@RequestMapping("/BeaconGroup")
public class BeaconGroupController {
    @Autowired
    private BeaconGroupService beaconGroupService;

    @Autowired
    private BeaconService beaconService;

    /**
     * Get the beacon group with specified ID
     *
     * @param id
     *     The ID of the group
     *
     * @return The beacon group
     */
    @RequestMapping(method = RequestMethod.GET, value = "/{id}", produces = "application/json")
    public ResponseEntity<BeaconGroup> viewBeaconGroup(@PathVariable String id) {
        Long beaconGroupIDAsLong;
        try {
            beaconGroupIDAsLong = Long.valueOf(id);
        } catch (NumberFormatException e) {
            return new ResponseEntity<BeaconGroup>(HttpStatus.BAD_REQUEST);
        }

        BeaconGroup beaconGroup = beaconGroupService.findById(beaconGroupIDAsLong);
        if (beaconGroup == null) {
            return new ResponseEntity<BeaconGroup>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<BeaconGroup>(beaconGroup, HttpStatus.OK);
    }

    /**
     * Get beacon objects that belong to a group.
     *
     * @param id
     *     The ID of the group
     *
     * @return The list of beacons that belong to the group with the specified ID
     */
    @RequestMapping(method = RequestMethod.GET, value = "/{id}/beacons", produces = "application/json")
    public ResponseEntity<List<Beacon>> viewBeaconGroupMembers(@PathVariable String id) {
        Long beaconGroupIDAsLong;
        try {
            beaconGroupIDAsLong = Long.valueOf(id);
        } catch (NumberFormatException e) {
            return new ResponseEntity<List<Beacon>>(HttpStatus.BAD_REQUEST);
        }

        BeaconGroup beaconGroup = beaconGroupService.findById(beaconGroupIDAsLong);
        if (beaconGroup == null) {
            return new ResponseEntity<List<Beacon>>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<List<Beacon>>(beaconGroup.getBeacons(), HttpStatus.OK);
    }

    /**
     * Add beacon to the specified beacon group.
     * <p/>
     * Can return 409 if beacon already has a group.
     * <p/>
     * Ex: "/BeaconGroup/1/add?beaconId=12"
     *
     * @param beaconGroupId
     *     The ID of the beacon group to add the beacon to
     * @param beaconId
     *     The ID of the beacon to add
     *
     * @return The added beacon group
     */
    @Transactional
    @RequestMapping(method = RequestMethod.POST, value = "/{beaconGroupId}/add")
    public ResponseEntity<BeaconGroup> addBeaconToGroup(@PathVariable String beaconGroupId, @RequestParam(value = "beaconId", required = true) String beaconId) {
        Long beaconGroupIDAsLong;
        try {
            beaconGroupIDAsLong = Long.valueOf(beaconGroupId);
        } catch (NumberFormatException e) {
            return new ResponseEntity<BeaconGroup>(HttpStatus.BAD_REQUEST);
        }

        BeaconGroup beaconGroup = beaconGroupService.findById(beaconGroupIDAsLong);
        if (beaconGroup == null) {
            return new ResponseEntity<BeaconGroup>(HttpStatus.NOT_FOUND);
        }

        Long beaconIDAsLong;
        try {
            beaconIDAsLong = Long.valueOf(beaconId);
        } catch (NumberFormatException e) {
            return new ResponseEntity<BeaconGroup>(HttpStatus.BAD_REQUEST);
        }

        Beacon beacon = beaconService.findById(beaconIDAsLong);
        if (beacon == null) {
            return new ResponseEntity<BeaconGroup>(HttpStatus.NOT_FOUND);
        }

        if (beacon.getGroup() != null) {
            return new ResponseEntity<BeaconGroup>(HttpStatus.CONFLICT);
        } else {

            beacon.setGroup(beaconGroup);
            beaconService.save(beacon);
            return new ResponseEntity<BeaconGroup>(beaconGroup, HttpStatus.OK);
        }
    }

    /**
     * Delete beacon from the specified beacon group.
     * <p/>
     * Can return 400 if beacon does not have a group.
     * <p/>
     * Ex: "/BeaconGroup/1/remove?beaconId=12"
     *
     * @param beaconGroupId
     *     The ID of the beacon group to remove the beacon from
     * @param beaconId
     *     The ID of the beacon to remove
     *
     * @return The removed beacon group
     */
    @Transactional
    @RequestMapping(method = RequestMethod.DELETE, value = "/{beaconGroupId}/remove")
    public ResponseEntity<BeaconGroup> removeBeaconFromGroup(@PathVariable String beaconGroupId, @RequestParam(value = "beaconId", required = true) String beaconId) {
        // TODO possible project association bug
        Long beaconGroupIDAsLong;
        try {
            beaconGroupIDAsLong = Long.valueOf(beaconGroupId);
        } catch (NumberFormatException e) {
            return new ResponseEntity<BeaconGroup>(HttpStatus.BAD_REQUEST);
        }

        BeaconGroup beaconGroup = beaconGroupService.findById(beaconGroupIDAsLong);
        if (beaconGroup == null) {
            return new ResponseEntity<BeaconGroup>(HttpStatus.NOT_FOUND);
        }

        Long beaconIDAsLong;
        try {
            beaconIDAsLong = Long.valueOf(beaconId);
        } catch (NumberFormatException e) {
            return new ResponseEntity<BeaconGroup>(HttpStatus.BAD_REQUEST);
        }

        Beacon beacon = beaconService.findById(beaconIDAsLong);
        if (beacon == null) {
            return new ResponseEntity<BeaconGroup>(HttpStatus.NOT_FOUND);
        }
        if (beacon.getGroup() == null) {
            return new ResponseEntity<BeaconGroup>(HttpStatus.BAD_REQUEST);
        }
        beacon.setGroup(null);
        beaconService.save(beacon);
        return new ResponseEntity<BeaconGroup>(beaconGroup, HttpStatus.OK);
    }

    /**
     * Delete the specified beacon group
     *
     * @param id
     *     The ID of the beacon group to delete
     *
     * @return The deleted beacon group
     */
    @Transactional
    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}", produces = "application/json")
    public ResponseEntity<BeaconGroup> deleteBeaconGroup(@PathVariable String id) {
        Long beaconGroupIDAsLong;
        try {
            beaconGroupIDAsLong = Long.valueOf(id);
        } catch (NumberFormatException e) {
            return new ResponseEntity<BeaconGroup>(HttpStatus.BAD_REQUEST);
        }

        DeleteResponse response = beaconGroupService.delete(beaconGroupIDAsLong);

        switch (response) {
            case DELETED:
                return new ResponseEntity<BeaconGroup>(HttpStatus.OK);
            case FORBIDDEN:
                return new ResponseEntity<BeaconGroup>(HttpStatus.FORBIDDEN);
            case NOT_FOUND:
                return new ResponseEntity<BeaconGroup>(HttpStatus.NOT_FOUND);
            case NOT_DELETED:
                return new ResponseEntity<BeaconGroup>(HttpStatus.NOT_ACCEPTABLE);
            default:
                return new ResponseEntity<BeaconGroup>(HttpStatus.I_AM_A_TEAPOT);
        }
    }
}
