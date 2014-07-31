package com.dteknoloji.controller;

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
import com.dteknoloji.service.BeaconService;

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

@Controller
@RequestMapping("/Beacon")
public class BeaconController {
    @Autowired
    private BeaconService beaconService;

    /**
     * Get all beacons (Optionally, all with matching criteria)
     *
     * @param uuid
     *     (Optional) The UUID of the beacon
     * @param major
     *     (Optional) The major of the beacon
     * @param minor
     *     (Optional) The minor of the beacon
     *
     * @return All existing beacons (Optionally, all that match the given criteria)
     */
    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<List<Beacon>> getAllBeacons(
        @RequestParam(value = "uuid", required = false, defaultValue = "") String uuid,
        @RequestParam(value = "major", required = false, defaultValue = "") String major,
        @RequestParam(value = "minor", required = false, defaultValue = "") String minor) {
        if (uuid.equals("") && major.equals("") && minor.equals("")) {
            return new ResponseEntity<List<Beacon>>(beaconService.findAll(), HttpStatus.OK);
        } else {
            return getBeaconsWithMatchingCriteria(uuid, major, minor);
        }
    }

    /**
     * Returns the list of beacons that match a given criteria
     *
     * @param uuid
     *     (Optional) The UUID of the beacon
     * @param major
     *     (Optional) The major of the beacon
     * @param minor
     *     (Optional) The minor of the beacon
     *
     * @return The list of beacons that match the given criteria
     */
    private ResponseEntity<List<Beacon>> getBeaconsWithMatchingCriteria(String uuid, String major, String minor) {
        List<Beacon> beacons = beaconService.findBeaconsBySpecs(uuid, major, minor);

        if (beacons.size() == 0) {
            return new ResponseEntity<List<Beacon>>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<List<Beacon>>(beacons, HttpStatus.OK);
        }
    }

    /**
     * Get the beacon with the specified ID
     *
     * @param id
     *     The ID of the beacon
     *
     * @return The beacon
     */
    @RequestMapping(method = RequestMethod.GET, value = "/{id}", produces = "application/json")
    public ResponseEntity<Beacon> viewBeacon(@PathVariable String id) {
        Beacon beacon = beaconService.findById(Long.valueOf(id));
        if (beacon == null) {
            return new ResponseEntity<Beacon>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Beacon>(beacon, HttpStatus.OK);
    }

    /**
     * Create a new beacon
     *
     * @param restBeacon
     *     The beacon as JSON object
     * @param builder
     *     The URI builder
     *
     * @return The created beacon
     */
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Beacon> createBeacon(@RequestBody Beacon restBeacon, UriComponentsBuilder builder) {
        try {
            Beacon newBeacon = beaconService.save(restBeacon);
            if (GlobalSettings.DEBUGGING) {
                System.out.println("Saved beacon with UUID = \'" + newBeacon.getUuid() + "\' major = \'" + newBeacon.getMajor() + "\' minor = \'" + newBeacon.getMinor() + "\'");
            }
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(builder.path("/Beacon/{id}").buildAndExpand(newBeacon.getBeaconId().toString()).toUri());
            return new ResponseEntity<Beacon>(newBeacon, headers, HttpStatus.CREATED);
        } catch (ConstraintViolationException | TransactionSystemException e) {
            if (GlobalSettings.DEBUGGING) {
                System.err.println("Unable to save beacon! Constraint violation detected!");
            }
            return new ResponseEntity<Beacon>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Create a multiple new beacons
     *
     * @param restBeacons
     *     The beacon list as JSON object
     *
     * @return The created beacons
     */
    @RequestMapping(method = RequestMethod.POST, value = "/import")
    public ResponseEntity<List<Beacon>> createMultipleBeacon(@RequestBody List<Beacon> restBeacons) {
        for (Beacon restBeacon : restBeacons) {
            try {
                Beacon newBeacon = beaconService.save(restBeacon);
                if (GlobalSettings.DEBUGGING) {
                    System.out.println("Saved beacon with UUID = \'" + newBeacon.getUuid() + "\' major = \'" + newBeacon.getMajor() + "\' minor = \'" + newBeacon.getMinor() + "\'");
                }
            } catch (ConstraintViolationException | TransactionSystemException e) {
                if (GlobalSettings.DEBUGGING) {
                    System.err.println("Unable to save beacon! Constraint violation detected!");
                }
                return new ResponseEntity<List<Beacon>>(HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity<List<Beacon>>(restBeacons, HttpStatus.CREATED);
    }

    /**
     * Delete the specified beacon
     *
     * @param id
     *     The ID of beacon to delete
     *
     * @return The deleted beacon
     */
    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}", produces = "application/json")
    public ResponseEntity<Beacon> deleteBeacon(@PathVariable String id) {

        Beacon beacon = beaconService.findById(Long.valueOf(id));
        if (beacon == null) {
            return new ResponseEntity<Beacon>(HttpStatus.NOT_FOUND);
        }

        boolean deleted = beaconService.delete(Long.valueOf(id));
        if (deleted) {
            return new ResponseEntity<Beacon>(beacon, HttpStatus.OK);
        }

        return new ResponseEntity<Beacon>(HttpStatus.FORBIDDEN);
    }

}
