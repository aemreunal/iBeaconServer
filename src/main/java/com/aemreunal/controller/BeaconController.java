package com.aemreunal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.aemreunal.domain.Beacon;
import com.aemreunal.service.BeaconService;

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
     * Get the beacon with the specified ID
     *
     * @param id
     *     The ID of the beacon
     *
     * @return The beacon
     */
    @RequestMapping(method = RequestMethod.GET, value = "/{id}", produces = "application/json")
    public ResponseEntity<Beacon> viewBeacon(@PathVariable String id) {
        Long beaconIDAsLong;
        try {
            beaconIDAsLong = Long.valueOf(id);
        } catch (NumberFormatException e) {
            return new ResponseEntity<Beacon>(HttpStatus.BAD_REQUEST);
        }

        Beacon beacon = beaconService.findById(beaconIDAsLong);
        if (beacon == null) {
            return new ResponseEntity<Beacon>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Beacon>(beacon, HttpStatus.OK);
    }
//
//    /**
//     * Create a multiple new beacons
//     *
//     * @param restBeacons
//     *     The beacon list as JSON object
//     *
//     * @return The created beacons
//     */
//    @RequestMapping(method = RequestMethod.POST, value = "/import")
//    public ResponseEntity<List<Beacon>> createMultipleBeacon(@RequestBody List<Beacon> restBeacons) {
//        for (Beacon restBeacon : restBeacons) {
//            try {
//                Beacon newBeacon = beaconService.save(restBeacon);
//                if (GlobalSettings.DEBUGGING) {
//                    System.out.println("Saved beacon with UUID = \'" + newBeacon.getUuid() + "\' major = \'" + newBeacon.getMajor() + "\' minor = \'" + newBeacon.getMinor() + "\'");
//                }
//            } catch (ConstraintViolationException | TransactionSystemException e) {
//                if (GlobalSettings.DEBUGGING) {
//                    System.err.println("Unable to save beacon! Constraint violation detected!");
//                }
//                return new ResponseEntity<List<Beacon>>(HttpStatus.BAD_REQUEST);
//            }
//        }
//        return new ResponseEntity<List<Beacon>>(restBeacons, HttpStatus.CREATED);
//    }

    /**
     * Delete the specified beacon
     *
     * @param id
     *     The ID of beacon to delete
     *
     * @return The deleted beacon
     */
    @Transactional
    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}", produces = "application/json")
    public ResponseEntity<Beacon> deleteBeacon(@PathVariable String id) {
        Long beaconIDAsLong;
        try {
            beaconIDAsLong = Long.valueOf(id);
        } catch (NumberFormatException e) {
            return new ResponseEntity<Beacon>(HttpStatus.BAD_REQUEST);
        }

        DeleteResponse response = beaconService.delete(beaconIDAsLong);

        switch (response) {
            case DELETED:
                return new ResponseEntity<Beacon>(HttpStatus.OK);
            case FORBIDDEN:
                return new ResponseEntity<Beacon>(HttpStatus.FORBIDDEN);
            case NOT_FOUND:
                return new ResponseEntity<Beacon>(HttpStatus.NOT_FOUND);
            case NOT_DELETED:
                return new ResponseEntity<Beacon>(HttpStatus.NOT_ACCEPTABLE);
            default:
                return new ResponseEntity<Beacon>(HttpStatus.I_AM_A_TEAPOT);
        }
    }

}
