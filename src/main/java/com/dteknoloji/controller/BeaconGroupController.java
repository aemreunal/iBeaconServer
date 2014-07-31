package com.dteknoloji.controller;

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
import javax.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import com.dteknoloji.config.GlobalSettings;
import com.dteknoloji.domain.Beacon;
import com.dteknoloji.domain.BeaconGroup;
import com.dteknoloji.service.BeaconGroupService;
import com.dteknoloji.service.BeaconService;

@Controller
@RequestMapping("/BeaconGroup")
public class BeaconGroupController {
    @Autowired
    private BeaconGroupService beaconGroupService;

    @Autowired
    private BeaconService beaconService;

    /**
     * Get all beacon groups
     *
     * @return All existing beacon groups
     */
    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<List<BeaconGroup>> getAllBeaconGroups() {
        return new ResponseEntity<List<BeaconGroup>>(beaconGroupService.findAll(), HttpStatus.OK);
    }

    /**
     * Get the beacon group with specified ID
     *
     * @param id The ID of the group
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
     * @param id The ID of the group
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
     * Create a new beacon group
     *
     * @param restBeaconGroup The beacon group as JSON object
     * @param builder
     * @return The created beacon group
     */
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<BeaconGroup> createBeaconGroup(@RequestBody BeaconGroup restBeaconGroup, UriComponentsBuilder builder) {
        try {
            BeaconGroup newBeaconGroup = beaconGroupService.save(restBeaconGroup);
            if (GlobalSettings.DEBUGGING) {
                System.out.println("Saved beacon group with ID = \'" + newBeaconGroup.getBeaconGroupId() + "\' name = \'" + newBeaconGroup.getName() + "\'");
            }
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(builder.path("/BeaconGroup/{id}").buildAndExpand(newBeaconGroup.getBeaconGroupId().toString()).toUri());
            return new ResponseEntity<BeaconGroup>(newBeaconGroup, headers, HttpStatus.CREATED);
        } catch (ConstraintViolationException | TransactionSystemException e) {
            if (GlobalSettings.DEBUGGING) {
                System.err.println("Unable to save beacon group! Constraint violation detected!");
            }
            return new ResponseEntity<BeaconGroup>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Add beacon to the specified beacon group.
     * <p/>
     * Can return 409 if beacon already has a group.
     * <p/>
     * Ex: "/BeaconGroup/1/add?beaconId=12"
     *
     * @param beaconGroupId The ID of the beacon group to add the beacon to
     * @param beaconId      The ID of the beacon to add
     * @return The added beacon group
     */
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
        } else {
            Beacon beacon = beaconService.findById(Long.valueOf(beaconId));
            if (beacon == null) {
                return new ResponseEntity<BeaconGroup>(HttpStatus.NOT_FOUND);
            } else {
                if (beacon.getGroup() != null) {
                    return new ResponseEntity<BeaconGroup>(HttpStatus.CONFLICT);
                } else {
                    beacon.setGroup(beaconGroup);
                    beaconGroup.getBeacons().add(beacon);
                    beaconService.save(beacon);
                    beaconGroupService.save(beaconGroup);
                    return new ResponseEntity<BeaconGroup>(beaconGroup, HttpStatus.OK);
                }
            }
        }
    }

    /**
     * Delete beacon from the specified beacon group.
     * <p/>
     * Can return 400 if beacon does not have a group.
     * <p/>
     * Ex: "/BeaconGroup/1/remove?beaconId=12"
     *
     * @param beaconGroupId The ID of the beacon group to remove the beacon from
     * @param beaconId      The ID of the beacon to remove
     * @return The removed beacon group
     */
    @RequestMapping(method = RequestMethod.DELETE, value = "/{beaconGroupId}/remove")
    public ResponseEntity<BeaconGroup> removeBeaconFromGroup(@PathVariable String beaconGroupId, @RequestParam(value = "beaconId", required = true) String beaconId) {
        BeaconGroup beaconGroup = beaconGroupService.findById(Long.valueOf(beaconGroupId));
        if (beaconGroup == null) {
            return new ResponseEntity<BeaconGroup>(HttpStatus.NOT_FOUND);
        } else {
            Beacon beacon = beaconService.findById(Long.valueOf(beaconId));
            if (beacon == null) {
                return new ResponseEntity<BeaconGroup>(HttpStatus.NOT_FOUND);
            } else {
                if (beacon.getGroup() == null) {
                    return new ResponseEntity<BeaconGroup>(HttpStatus.BAD_REQUEST);
                } else {
                    beacon.setGroup(null);
                    beaconGroup.getBeacons().remove(beacon);
                    beaconService.save(beacon);
                    beaconGroupService.save(beaconGroup);
                    return new ResponseEntity<BeaconGroup>(beaconGroup, HttpStatus.OK);
                }
            }
        }
    }

    /**
     * Delete the specified beacon group
     *
     * @param id The ID of the beacon group to delete
     * @return The deleted beacon group
     */
    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}", produces = "application/json")
    public ResponseEntity<BeaconGroup> deleteBeaconGroup(@PathVariable String id) {
        BeaconGroup beaconGroup = beaconGroupService.findById(Long.valueOf(id));
        if (beaconGroup == null) {
            return new ResponseEntity<BeaconGroup>(HttpStatus.NOT_FOUND);
        }
        boolean deleted = beaconGroupService.delete(Long.valueOf(id));
        if (deleted) {
            return new ResponseEntity<BeaconGroup>(beaconGroup, HttpStatus.OK);
        } else {
            return new ResponseEntity<BeaconGroup>(beaconGroup, HttpStatus.FORBIDDEN);
        }
    }
}
