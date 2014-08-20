package com.aemreunal.controller.beacon;

import java.util.List;
import javax.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import com.aemreunal.config.GlobalSettings;
import com.aemreunal.controller.BeaconGroupController;
import com.aemreunal.controller.project.ProjectController;
import com.aemreunal.controller.user.UserController;
import com.aemreunal.domain.Beacon;
import com.aemreunal.service.BeaconService;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

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
@RequestMapping(GlobalSettings.BEACON_PATH_MAPPING)
public class BeaconController {
    @Autowired
    private BeaconService beaconService;

    /**
     * Get all the {@link com.aemreunal.domain.Beacon beacons} that belong to the
     * specified {@link com.aemreunal.domain.Project project}. Returns an empty list if no
     * beacons are present in the project.
     * <p/>
     * Optionally, certain parameters may be specified to get a refined search. These
     * paramters are: <li><b>UUID:</b> The UUID constraint. Can be specified with the
     * "{@code?uuid=...}" URI parameter. Any continuous part of thes UUID attribute may be
     * specified in the constraint.</li> <li><b>Major:</b> The Major constraint. Can be
     * specified with the "{@code?major=...}" URI parameter.</li> <li><b>Minor:</b> The
     * Minor constraint. Can be specified with the "{@code?minor=...}" URI
     * parameter.</li>
     *
     * @param username
     *     The username of the owner of the project
     * @param projectId
     *     The ID of the project
     * @param uuid
     *     (Optional) The UUID constraint for the beacon search
     * @param major
     *     (Optional) The Major constraint for the beacon search
     * @param minor
     *     (Optional) The Minor constraint for the beacon search
     *
     * @return If no optional parameters are specified, returns all the beacons that
     * belong to a project (an empty list if the project has no beacons). If optional
     * constraints are given, returns a list of all the matching beacons or throws a
     * {@link com.aemreunal.exception.beacon.BeaconNotFoundException
     * BeaconNotFoundException} if no beacons match the constraints.
     */
    @Transactional
    @RequestMapping(method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public ResponseEntity<List<Beacon>> getBeaconsOfProject(@PathVariable String username,
                                                            @PathVariable Long projectId,
                                                            @RequestParam(value = "uuid", required = false, defaultValue = "") String uuid,
                                                            @RequestParam(value = "major", required = false, defaultValue = "") String major,
                                                            @RequestParam(value = "minor", required = false, defaultValue = "") String minor) {
        if (uuid.equals("") && major.equals("") && minor.equals("")) {
            List<Beacon> beaconList = beaconService.getBeaconsOfProject(username, projectId);
            return new ResponseEntity<List<Beacon>>(beaconList, HttpStatus.OK);
        } else {
            List<Beacon> beacons = beaconService.findBeaconsBySpecs(username, projectId, uuid, major, minor);
            return new ResponseEntity<List<Beacon>>(beacons, HttpStatus.OK);
        }
    }

    /**
     * Get the beacon with the specified ID
     *
     * @param username
     *     The username of the owner of the project
     * @param projectId
     *     The ID of the project
     * @param beaconId
     *     The ID of the beacon
     *
     * @return The beacon
     */
    @RequestMapping(method = RequestMethod.GET, value = GlobalSettings.BEACON_ID_MAPPING, produces = "application/json;charset=UTF-8")
    public ResponseEntity<Beacon> getBeacon(@PathVariable String username,
                                            @PathVariable Long projectId,
                                            @PathVariable Long beaconId) {
        Beacon beacon = beaconService.findBeaconInProject(username, projectId, beaconId);
        addLinks(username, projectId, beacon);
        return new ResponseEntity<Beacon>(beacon, HttpStatus.OK);
    }

    private void addLinks(String username, Long projectId, Beacon beacon) {
        beacon.add(ControllerLinkBuilder.linkTo(methodOn(BeaconController.class).getBeacon(username, projectId, beacon.getBeaconId())).withSelfRel());
        beacon.add(ControllerLinkBuilder.linkTo(methodOn(UserController.class).getUserByUsername(username)).withRel("owner"));
        beacon.add(ControllerLinkBuilder.linkTo(methodOn(ProjectController.class).getProjectById(username, projectId)).withRel("project"));
        if (beacon.getGroup() != null) {
            beacon.add(ControllerLinkBuilder.linkTo(methodOn(BeaconGroupController.class).viewBeaconGroup(username, projectId, beacon.getGroup().getBeaconGroupId())).withRel("group"));
        }
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
    @RequestMapping(method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public ResponseEntity<Beacon> createBeaconInProject(@PathVariable String username,
                                                        @PathVariable Long projectId,
                                                        @RequestBody Beacon restBeacon,
                                                        UriComponentsBuilder builder)
        throws ConstraintViolationException {
        Beacon savedBeacon = beaconService.save(username, projectId, restBeacon);

        if (GlobalSettings.DEBUGGING) {
            System.out.println("Saved beacon with UUID = \'" + savedBeacon.getUuid() +
                                   "\' major = \'" + savedBeacon.getMajor() +
                                   "\' minor = \'" + savedBeacon.getMinor() +
                                   "\' in project with ID = \'" + projectId + "\'");
        }
        return buildCreateResponse(username, builder, savedBeacon);
    }

    private ResponseEntity<Beacon> buildCreateResponse(String username, UriComponentsBuilder builder, Beacon savedBeacon) {
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(builder.path(GlobalSettings.BEACON_SPECIFIC_MAPPING)
                                   .buildAndExpand(
                                       username,
                                       savedBeacon.getProject().getProjectId().toString(),
                                       savedBeacon.getBeaconId().toString())
                                   .toUri());
        return new ResponseEntity<Beacon>(savedBeacon, headers, HttpStatus.CREATED);
    }

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
    @RequestMapping(method = RequestMethod.DELETE, value = GlobalSettings.BEACON_ID_MAPPING)
    public ResponseEntity<Beacon> deleteBeacon(@PathVariable String username,
                                               @PathVariable Long projectId,
                                               @PathVariable Long beaconId,
                                               @RequestParam(value = "confirm", required = true) String confirmation) {
        if (confirmation.toLowerCase().equals("yes")) {
            Beacon deletedBeacon = beaconService.delete(username, projectId, beaconId);
            return new ResponseEntity<Beacon>(deletedBeacon, HttpStatus.OK);
        } else {
            return new ResponseEntity<Beacon>(HttpStatus.PRECONDITION_FAILED);
        }
    }

}
