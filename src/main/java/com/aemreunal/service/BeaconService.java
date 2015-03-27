package com.aemreunal.service;

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

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.aemreunal.config.GlobalSettings;
import com.aemreunal.domain.Beacon;
import com.aemreunal.domain.Connection;
import com.aemreunal.domain.Region;
import com.aemreunal.domain.Scenario;
import com.aemreunal.exception.MalformedRequestException;
import com.aemreunal.exception.beacon.BeaconAlreadyExistsException;
import com.aemreunal.exception.beacon.BeaconNotFoundException;
import com.aemreunal.exception.project.ProjectNotFoundException;
import com.aemreunal.exception.region.*;
import com.aemreunal.helper.JsonBuilder;
import com.aemreunal.repository.beacon.BeaconRepo;
import com.aemreunal.repository.beacon.BeaconSpecs;

@Transactional
@Service
public class BeaconService {
    @Autowired
    private RegionService regionService;

    @Autowired
    private ConnectionService connectionService;

    @Autowired
    private BeaconRepo beaconRepo;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    /**
     * Saves/updates the given beacon
     *
     * @param beacon
     *         The beacon to save/update
     *
     * @return The saved/updated beacon
     */
    public Beacon save(String username, Long projectId, Long regionId, Beacon beacon)
            throws ConstraintViolationException, BeaconAlreadyExistsException, MalformedRequestException {
        GlobalSettings.log("Saving beacon with ID = \'" + beacon.getBeaconId() + "\'");
        Region region = regionService.getRegion(username, projectId, regionId);
        if (beacon.getRegion() == null) {
            // This means it hasn't been created yet
            validateBeacon(username, projectId, regionId, beacon, region);
            beacon.setRegion(region);
        }
        return beaconRepo.save(beacon);
    }

    private void validateBeacon(String username, Long projectId, Long regionId, Beacon beacon, Region region)
    throws BeaconAlreadyExistsException, MalformedRequestException {
        if (beaconExists(username, projectId, regionId, beacon)) {
            // First, verify the beacon doesn't already exist
            throw new BeaconAlreadyExistsException(beacon);
        } else if (!region.beaconCoordsAreValid(beacon)) {
            // Check whether the beacon coordinates are valid
            throw new MalformedRequestException();
        }
    }

    private boolean beaconExists(String username, Long projectId, Long regionId, Beacon beacon) {
        Set<Beacon> beacons;
        try {
            beacons = findBeaconsBySpecs(username, projectId, regionId, beacon.getUuid(), beacon.getMajor(), beacon.getMinor(), null);
            return beacons.size() != 0;
        } catch (BeaconNotFoundException e) {
            GlobalSettings.err("No such beacon has been found. Will return false;");
        }
        return false;
    }

    /**
     * Finds beacons conforming to given specifications
     *
     * @param username
     *         The username constraint
     * @param projectId
     *         The project ID constraint
     * @param regionId
     *         The region ID constraint
     * @param uuid
     *         The UUID field constraint
     * @param major
     *         The Major field constraint
     * @param minor
     *         The Minor field constraint
     * @param designated
     *         The designated field constraint
     *
     * @return The list of beacons conforming to given constraints
     */
    @Transactional(readOnly = true)
    public Set<Beacon> findBeaconsBySpecs(String username, Long projectId, Long regionId, String uuid, Integer major, Integer minor, Boolean designated)
            throws BeaconNotFoundException {
        GlobalSettings.log("Finding beacons with UUID = \'" + uuid + "\' major = \'" + major + "\' minor = \'" + minor + "\' designated = \'" + designated + "\'");
        List<Beacon> beacons = beaconRepo.findAll(BeaconSpecs.beaconWithSpecification(username, projectId, regionId, uuid, major, minor, designated));
//        if (beacons.size() == 0) {
//            throw new BeaconNotFoundException();
//        }
        return beacons.stream().collect(Collectors.toSet());
    }

    @Transactional(readOnly = true)
    public Beacon queryForBeacon(String uuid, Integer major, Integer minor, String projectSecret)
    throws BeaconNotFoundException {
        List<Beacon> beacons = beaconRepo.findAll(BeaconSpecs.beaconWithSpecification(null, null, null, uuid, major, minor, null));
        for (Beacon beacon : beacons) {
            if (passwordEncoder.matches(projectSecret, beacon.getRegion().getProject().getProjectSecret())) {
                return beacon;
            }
        }
        throw new BeaconNotFoundException();
    }

    /**
     * Finds the {@link com.aemreunal.domain.Beacon beacon} with the specified beaconId in
     * a {@link com.aemreunal.domain.Region region}.
     *
     * @param username
     *         The username of the {@link com.aemreunal.domain.User owner} of the project
     * @param projectId
     *         The ID of the project
     * @param regionId
     *         The ID of the region
     * @param beaconId
     *         The ID of the beacon to find
     *
     * @return The beacon
     *
     * @throws com.aemreunal.exception.beacon.BeaconNotFoundException
     *         If the specified beacon does not exist.
     * @throws com.aemreunal.exception.project.ProjectNotFoundException
     *         If the specified project does not exist.
     * @throws com.aemreunal.exception.region.RegionNotFoundException
     *         If the specified region does not exist.
     */
    @Transactional(readOnly = true)
    public Beacon getBeacon(String username, Long projectId, Long regionId, Long beaconId)
    throws BeaconNotFoundException, ProjectNotFoundException, RegionNotFoundException {
        GlobalSettings.log("Finding beacon with ID = \'" + beaconId + "\' in project = \'" + projectId + "\' and in region = \'" + regionId + "\'");
        Region region = regionService.getRegion(username, projectId, regionId);
        Beacon beacon = beaconRepo.findByBeaconIdAndRegion(beaconId, region);
        if (beacon == null) {
            throw new BeaconNotFoundException(beaconId);
        }
        return beacon;
    }

    /**
     * Returns the list of {@link com.aemreunal.domain.Beacon beacons} that belong to a
     * {@link com.aemreunal.domain.Region region}.
     *
     * @param username
     *         The username of the {@link com.aemreunal.domain.User owner} of the
     *         project.
     * @param projectId
     *         The ID of the project.
     * @param regionId
     *         The ID of the region.
     *
     * @return The list of beacons that belong to a region. Returns an empty list if the
     * region has no beacons.
     */
    @Transactional(readOnly = true)
    public Set<Beacon> getBeaconsOfRegion(String username, Long projectId, Long regionId) {
        return regionService.getMembersOfRegion(username, projectId, regionId);
    }

    @Transactional(readOnly = true)
    public Set<Beacon> getDesignatedBeaconsOfRegion(String username, Long projectId, Long regionId) {
        return this.findBeaconsBySpecs(username, projectId, regionId, null, null, null, Boolean.TRUE);
    }

    public JSONObject createConnection(String username, Long projectId, Long regionOneId, Long beaconOneId, Long regionTwoId, Long beaconTwoId, MultipartFile imageMultipartFile) throws WrongFileTypeSubmittedException, MapImageSaveException, ImageDeleteException, MultipartFileReadException {
        Connection connection = connectionService.createNewConnection(username, projectId, beaconOneId, regionOneId, beaconTwoId, regionTwoId, imageMultipartFile);
        return new JsonBuilder(JsonBuilder.OBJECT).addToObj("beacons", connection.getBeaconIdsAsJson()).buildObj();
    }

    public Beacon addConnection(String username, Long projectId, Long regionId, Long beaconId, Connection connection) {
        Beacon beacon = this.getBeacon(username, projectId, regionId, beaconId);
        beacon.addConnection(connection);
        return this.save(username, projectId, regionId, beacon);
    }

    @Transactional(readOnly = true)
    public Set<Connection> getConnectionsOfBeacon(String username, Long projectId, Long regionId, Long beaconId) {
        Beacon beacon = this.getBeacon(username, projectId, regionId, beaconId);
        return beacon.getConnections();
    }

    public void setBeaconScenario(String username, Long projectId, Long regionId, Beacon beacon, Scenario scenario) {
        beacon.setScenario(scenario);
        save(username, projectId, regionId, beacon);
    }

    public Beacon delete(String username, Long projectId, Long regionId, Long beaconId) {
        GlobalSettings.log("Deleting beacon with ID = \'" + beaconId + "\'");
        // Retrieving beacon to ensure that beacon exists and is part of this user/project/region etc.
        Beacon beacon = getBeacon(username, projectId, regionId, beaconId);
        beaconRepo.delete(beaconId);
        return beacon;
    }
}
