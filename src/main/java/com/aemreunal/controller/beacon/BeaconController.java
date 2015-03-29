package com.aemreunal.controller.beacon;

/*
 * *********************** *
 * Copyright (c) 2015      *
 *                         *
 * This code belongs to:   *
 *                         *
 * @author Ahmet Emre Ünal *
 * S001974                 *
 *                         *
 * aemreunal@gmail.com     *
 * emre.unal@ozu.edu.tr    *
 *                         *
 * aemreunal.com           *
 * *********************** *
 */

import net.minidev.json.JSONObject;

import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;
import com.aemreunal.config.GlobalSettings;
import com.aemreunal.controller.project.ProjectController;
import com.aemreunal.controller.region.RegionController;
import com.aemreunal.controller.scenario.ScenarioController;
import com.aemreunal.controller.user.UserController;
import com.aemreunal.domain.Beacon;
import com.aemreunal.exception.connection.ConnectionExistsException;
import com.aemreunal.exception.connection.ConnectionNotFoundException;
import com.aemreunal.exception.imageStorage.ImageDeleteException;
import com.aemreunal.exception.imageStorage.ImageLoadException;
import com.aemreunal.exception.imageStorage.ImageSaveException;
import com.aemreunal.exception.region.*;
import com.aemreunal.service.BeaconService;
import com.aemreunal.service.ConnectionService;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Controller
@RequestMapping(GlobalSettings.BEACON_PATH_MAPPING)
public class BeaconController {
    @Autowired
    private BeaconService beaconService;

    @Autowired
    private ConnectionService connectionService;

    /**
     * Get all the {@link com.aemreunal.domain.Beacon beacons} that belong to the
     * specified {@link com.aemreunal.domain.Project project}. Returns an empty list if no
     * beacons are present in the project.
     * <p>
     * {@literal @}Transactional mark via http://stackoverflow.com/questions/11812432/spring-data-hibernate
     * <p>
     * Optionally, certain parameters may be specified to get a refined search. These
     * paramters are: <li><b>UUID:</b> The UUID constraint. Can be specified with the
     * "{@code?uuid=...}" URI parameter. Any continuous part of thes UUID attribute may be
     * specified in the constraint.</li> <li><b>Major:</b> The Major constraint. Can be
     * specified with the "{@code?major=...}" URI parameter.</li> <li><b>Minor:</b> The
     * Minor constraint. Can be specified with the "{@code?minor=...}" URI
     * parameter.</li>
     *
     * @param username
     *         The username of the owner of the project
     * @param projectId
     *         The ID of the project
     * @param uuid
     *         (Optional) The UUID constraint for the beacon search
     * @param major
     *         (Optional) The Major constraint for the beacon search
     * @param minor
     *         (Optional) The Minor constraint for the beacon search
     *
     * @return If no optional parameters are specified, returns all the beacons that
     * belong to a project (an empty list if the project has no beacons). If optional
     * constraints are given, returns a list of all the matching beacons or throws a
     * {@link com.aemreunal.exception.beacon.BeaconNotFoundException
     * BeaconNotFoundException} if no beacons match the constraints.
     */
    @Transactional
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Set<Beacon>> getBeaconsOfRegion(@PathVariable String username,
                                                          @PathVariable Long projectId,
                                                          @PathVariable Long regionId,
                                                          @RequestParam(value = "uuid", required = false) String uuid,
                                                          @RequestParam(value = "major", required = false) Integer major,
                                                          @RequestParam(value = "minor", required = false) Integer minor,
                                                          @RequestParam(value = "designated", required = false) Boolean designated) {
        if (uuid == null && major == null && minor == null && designated == null) {
            Set<Beacon> beaconSet = beaconService.getBeaconsOfRegion(username, projectId, regionId);
            return new ResponseEntity<Set<Beacon>>(beaconSet, HttpStatus.OK);
        } else {
            Set<Beacon> beacons = beaconService.findBeaconsBySpecs(username, projectId, regionId, uuid, major, minor, designated);
            return new ResponseEntity<Set<Beacon>>(beacons, HttpStatus.OK);
        }
    }

    /**
     * Get the beacon with the specified ID
     *
     * @param username
     *         The username of the owner of the project
     * @param projectId
     *         The ID of the project
     * @param beaconId
     *         The ID of the beacon
     *
     * @return The beacon
     */
    @RequestMapping(method = RequestMethod.GET, value = GlobalSettings.BEACON_ID_MAPPING, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Beacon> getBeacon(@PathVariable String username,
                                            @PathVariable Long projectId,
                                            @PathVariable Long regionId,
                                            @PathVariable Long beaconId) {
        Beacon beacon = beaconService.getBeacon(username, projectId, regionId, beaconId);
        addLinks(username, projectId, regionId, beacon);
        return new ResponseEntity<Beacon>(beacon, HttpStatus.OK);
    }

    private void addLinks(String username, Long projectId, Long regionId, Beacon beacon) {
        beacon.add(ControllerLinkBuilder.linkTo(methodOn(BeaconController.class).getBeacon(username, projectId, regionId, beacon.getBeaconId())).withSelfRel());
        beacon.add(ControllerLinkBuilder.linkTo(methodOn(UserController.class).getUserByUsername(username)).withRel("owner"));
        beacon.add(ControllerLinkBuilder.linkTo(methodOn(ProjectController.class).getProjectById(username, projectId)).withRel("project"));
        beacon.add(ControllerLinkBuilder.linkTo(methodOn(RegionController.class).getRegion(username, projectId, beacon.getRegion().getRegionId())).withRel("region"));
        if (beacon.getScenario() != null) {
            beacon.add(ControllerLinkBuilder.linkTo(methodOn(ScenarioController.class).getScenario(username, projectId, beacon.getScenario().getScenarioId())).withRel("scenario"));
        }
    }

    /**
     * Create a new beacon in region.
     * <p>
     * Beacon creation request JSON:
     * <pre>
     * {
     *   "uuid":"12345678-1234-1234-1234-123456789012",
     *   "major":"1",
     *   "minor":"2",
     *   "xCoordinate":"150"
     *   "yCoordinate":"120"
     *   "designated":"true"
     *   "description":"Beacon description"
     * }
     * </pre>
     *
     * @param username
     *         The username of the owner of the project
     * @param projectId
     *         The ID of the project to create the beacon in
     * @param regionId
     *         The ID of the region
     * @param beaconFromJson
     *         The beacon parsed from the JSON object
     * @param builder
     *         The URI builder for post-creation redirect
     *
     * @return The created beacon
     */
    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Beacon> createBeacon(@PathVariable String username,
                                               @PathVariable Long projectId,
                                               @PathVariable Long regionId,
                                               @RequestBody Beacon beaconFromJson,
                                               UriComponentsBuilder builder) {
        Beacon savedBeacon = beaconService.save(username, projectId, regionId, beaconFromJson);
        GlobalSettings.log("Saved beacon with UUID = \'" + savedBeacon.getUuid() +
                                   "\' major = \'" + savedBeacon.getMajor() +
                                   "\' minor = \'" + savedBeacon.getMinor() +
                                   "\' in project with ID = \'" + projectId + "\'");
        addLinks(username, projectId, regionId, savedBeacon);
        return buildCreateResponse(username, builder, savedBeacon);
    }

    private ResponseEntity<Beacon> buildCreateResponse(String username, UriComponentsBuilder builder, Beacon savedBeacon) {
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(builder.path(GlobalSettings.BEACON_SPECIFIC_MAPPING)
                                   .buildAndExpand(
                                           username,
                                           savedBeacon.getRegion().getProject().getProjectId().toString(),
                                           savedBeacon.getRegion().getRegionId().toString(),
                                           savedBeacon.getBeaconId().toString())
                                   .toUri());
        return new ResponseEntity<Beacon>(savedBeacon, headers, HttpStatus.CREATED);
    }

    /**
     * Create a connection between two beacons.
     *
     * @param username
     *         The username of the owner of the project.
     * @param projectId
     *         The ID of the project.
     * @param regionOneId
     *         The ID of the region.
     * @param beaconOneId
     *         The ID of one of the two beacons.
     * @param regionTwoId
     *         The ID of the region the other of the two beacons' is in.
     * @param beaconTwoId
     *         The ID of the other of the two beacons.
     * @param imageMultipartFile
     *         The connection navigation image file as a {@link org.springframework.web.multipart.MultipartFile
     *         MultipartFile}.
     *
     * @return The list of beacon IDs that were connected.
     *
     * @throws WrongFileTypeSubmittedException
     *         The file type of the {@link org.springframework.web.multipart.MultipartFile
     *         MultipartFile} file submitted as the connection image is of some other type
     *         than JPEG, GIF, or PNG.
     * @throws ImageSaveException
     *         If the server is unable to save the connection image.
     * @throws ImageDeleteException
     *         If the connection image being replaced couldn't be deleted by the server.
     * @throws MultipartFileReadException
     *         If the Multipart file couldn't be read.
     */
    @RequestMapping(method = RequestMethod.POST, value = GlobalSettings.BEACON_CONNECTION_MAPPING, consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JSONObject> connectToBeacon(@PathVariable String username,
                                                      @PathVariable Long projectId,
                                                      @PathVariable(value = "regionId") Long regionOneId,
                                                      @PathVariable(value = "beaconId") Long beaconOneId,
                                                      @RequestParam("region2id") Long regionTwoId,
                                                      @RequestParam("beacon2id") Long beaconTwoId,
                                                      @RequestPart(value = "image") MultipartFile imageMultipartFile)
            throws WrongFileTypeSubmittedException, ImageSaveException, ImageDeleteException, MultipartFileReadException, ConnectionExistsException {
        // TODO check if the same connection already exists
        JSONObject connection = beaconService.createConnection(username, projectId, regionOneId, beaconOneId, regionTwoId, beaconTwoId, imageMultipartFile);
        return new ResponseEntity<JSONObject>(connection, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.GET, value = GlobalSettings.BEACON_CONNECTION_MAPPING, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    // When ConnectionNotFound is triggered as a result of this method, an HttpMediaTypeNotAcceptableException
    // exception is raised. It's not raised if the exception handler also returns ResponseEntity<byte[]>.
    // TODO Fix.
    public ResponseEntity<byte[]> downloadConnectionImage(@PathVariable String username,
                                                          @PathVariable Long projectId,
                                                          @PathVariable(value = "regionId") Long regionOneId,
                                                          @PathVariable(value = "beaconId") Long beaconOneId,
                                                          @RequestParam("region2id") Long regionTwoId,
                                                          @RequestParam("beacon2id") Long beaconTwoId)
    throws ConnectionNotFoundException, ImageLoadException {
        byte[] connectionImage = connectionService.getConnectionImage(username, projectId, regionOneId, beaconOneId, regionTwoId, beaconTwoId);
        return new ResponseEntity<byte[]>(connectionImage, HttpStatus.OK);
    }

    /**
     * Delete the specified beacon
     * <p>
     * To delete the beacon, confirmation must be supplied as a URI parameter, in the form
     * of "?confirm=yes". If not supplied, the beacon will not be deleted.
     *
     * @param projectId
     *         The ID of the project
     * @param beaconId
     *         The ID of the beacon
     *
     * @return The status of deletion action
     */
    @RequestMapping(method = RequestMethod.DELETE, value = GlobalSettings.BEACON_ID_MAPPING)
    public ResponseEntity<Beacon> deleteBeacon(@PathVariable String username,
                                               @PathVariable Long projectId,
                                               @PathVariable Long regionId,
                                               @PathVariable Long beaconId,
                                               @RequestParam(value = "confirm", required = true) String confirmation) {
        if (confirmation.toLowerCase().equals("yes")) {
            Beacon deletedBeacon = beaconService.delete(username, projectId, regionId, beaconId);
            return new ResponseEntity<Beacon>(deletedBeacon, HttpStatus.OK);
        } else {
            return new ResponseEntity<Beacon>(HttpStatus.PRECONDITION_FAILED);
        }
    }

}
