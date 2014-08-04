package com.aemreunal.controller;

import java.util.List;
import javax.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import com.aemreunal.config.GlobalSettings;
import com.aemreunal.domain.Beacon;
import com.aemreunal.domain.Project;
import com.aemreunal.service.BeaconService;
import com.aemreunal.service.ProjectService;

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
@RequestMapping("/Project/{projectId}/Beacon")
public class BeaconController {
    @Autowired
    private ProjectService projectService;

    @Autowired
    private BeaconService beaconService;

    /**
     * Get beacons that belong to a project.
     *
     * @param projectId
     *     The ID of the project
     *
     * @return The list of beacons that belong to the project with the specified ID
     */
    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<List<Beacon>> viewBeaconsOfProject(
        @PathVariable String projectId,
        @RequestParam(value = "uuid", required = false, defaultValue = "") String uuid,
        @RequestParam(value = "major", required = false, defaultValue = "") String major,
        @RequestParam(value = "minor", required = false, defaultValue = "") String minor) {

        Long projectIDAsLong;
        try {
            projectIDAsLong = Long.valueOf(projectId);
        } catch (NumberFormatException e) {
            return new ResponseEntity<List<Beacon>>(HttpStatus.BAD_REQUEST);
        }

        Project project = projectService.findById(projectIDAsLong);
        if (project == null) {
            return new ResponseEntity<List<Beacon>>(HttpStatus.NOT_FOUND);
        }

        if (uuid.equals("") && major.equals("") && minor.equals("")) {
            return new ResponseEntity<List<Beacon>>(project.getBeacons(), HttpStatus.OK);
        } else {
            return getBeaconsWithMatchingCriteria(projectIDAsLong, uuid, major, minor);
        }
    }

    /**
     * Returns the list of beacons that match a given criteria
     *
     * @param projectId
     *     The project ID to search
     * @param uuid
     *     (Optional) The UUID of the beacon
     * @param major
     *     (Optional) The major of the beacon
     * @param minor
     *     (Optional) The minor of the beacon
     *
     * @return The list of beacons that match the given criteria
     */
    private ResponseEntity<List<Beacon>> getBeaconsWithMatchingCriteria(Long projectId, String uuid, String major, String minor) {
        List<Beacon> beacons = beaconService.findBeaconsBySpecs(projectId, uuid, major, minor);

        if (beacons.size() == 0) {
            return new ResponseEntity<List<Beacon>>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<List<Beacon>>(beacons, HttpStatus.OK);
        }
    }

    /**
     * Get the beacon with the specified ID
     *
     * @param projectId
     *     The ID of the project
     * @param beaconId
     *     The ID of the beacon
     *
     * @return The beacon
     */
    @RequestMapping(method = RequestMethod.GET, value = "/{beaconId}", produces = "application/json")
    public ResponseEntity<Beacon> viewBeacon(@PathVariable String projectId, @PathVariable String beaconId) {
        Long projectIDAsLong;
        try {
            projectIDAsLong = Long.valueOf(projectId);
        } catch (NumberFormatException e) {
            return new ResponseEntity<Beacon>(HttpStatus.BAD_REQUEST);
        }

        Long beaconIDAsLong;
        try {
            beaconIDAsLong = Long.valueOf(beaconId);
        } catch (NumberFormatException e) {
            return new ResponseEntity<Beacon>(HttpStatus.BAD_REQUEST);
        }

        Project project = projectService.findById(projectIDAsLong);
        if (project == null) {
            return new ResponseEntity<Beacon>(HttpStatus.NOT_FOUND);
        }

        Beacon beacon = beaconService.findById(beaconIDAsLong);
        if (beacon == null) {
            return new ResponseEntity<Beacon>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Beacon>(beacon, HttpStatus.OK);
    }

    /**
     * Create a new beacon in project
     * <p/>
     * {@literal @}Transactional mark via http://stackoverflow.com/questions/11812432/spring-data-hibernate
     *
     * @param projectId
     *     The ID of the project to create the beacon in
     * @param restBeacon
     *     The beacon as JSON object
     * @param builder
     *     The URI builder for post-creation redirect
     *
     * @return The created beacon
     */
    @Transactional
    @RequestMapping(method = RequestMethod.POST, value = "/Create", produces = "application/json")
    public ResponseEntity<Beacon> createBeaconInProject(
        @PathVariable String projectId,
        @RequestBody Beacon restBeacon, UriComponentsBuilder builder) {
        Long projectIDAsLong;
        try {
            projectIDAsLong = Long.valueOf(projectId);
        } catch (NumberFormatException e) {
            return new ResponseEntity<Beacon>(HttpStatus.BAD_REQUEST);
        }

        Project project = projectService.findById(projectIDAsLong);
        if (project == null) {
            return new ResponseEntity<Beacon>(HttpStatus.NOT_FOUND);
        }

        restBeacon.setProject(project);

        Beacon newBeacon;
        try {
            newBeacon = beaconService.save(restBeacon);
        } catch (ConstraintViolationException | TransactionSystemException e) {
            if (GlobalSettings.DEBUGGING) {
                System.err.println("Unable to save beacon! Constraint violation detected!");
            }
            return new ResponseEntity<Beacon>(HttpStatus.BAD_REQUEST);
        }

        if (GlobalSettings.DEBUGGING) {
            System.out.println("Saved beacon with UUID = \'" + newBeacon.getUuid() +
                "\' major = \'" + newBeacon.getMajor() +
                "\' minor = \'" + newBeacon.getMinor() +
                "\' in project with ID = \'" + projectIDAsLong + "\'");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(builder.path("/Beacon/{id}").buildAndExpand(newBeacon.getBeaconId().toString()).toUri());
        return new ResponseEntity<Beacon>(newBeacon, headers, HttpStatus.CREATED);
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
     * <p/>
     * To delete the beacon, confirmation must be supplied as a URI parameter, in the form
     * of "?confirm=yes". If not supplied, the beacon will not be deleted.
     *
     * @param projectId
     *     The ID of the project
     * @param beaconId
     *     The ID of the beacon
     *
     * @return The status of deletion action
     */
    @Transactional
    @RequestMapping(method = RequestMethod.DELETE, value = "/{beaconId}", produces = "application/json")
    public ResponseEntity<Beacon> deleteBeacon(@PathVariable String projectId, @PathVariable String beaconId) {
        Long projectIDAsLong;
        try {
            projectIDAsLong = Long.valueOf(projectId);
        } catch (NumberFormatException e) {
            return new ResponseEntity<Beacon>(HttpStatus.BAD_REQUEST);
        }

        Long beaconIDAsLong;
        try {
            beaconIDAsLong = Long.valueOf(beaconId);
        } catch (NumberFormatException e) {
            return new ResponseEntity<Beacon>(HttpStatus.BAD_REQUEST);
        }

        Project project = projectService.findById(projectIDAsLong);
        if (project == null) {
            return new ResponseEntity<Beacon>(HttpStatus.NOT_FOUND);
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
