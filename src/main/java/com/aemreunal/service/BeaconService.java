package com.aemreunal.service;

import java.util.List;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.aemreunal.config.GlobalSettings;
import com.aemreunal.domain.Beacon;
import com.aemreunal.domain.Region;
import com.aemreunal.domain.Scenario;
import com.aemreunal.exception.MalformedRequestException;
import com.aemreunal.exception.beacon.BeaconAlreadyExistsException;
import com.aemreunal.exception.beacon.BeaconNotFoundException;
import com.aemreunal.exception.project.ProjectNotFoundException;
import com.aemreunal.exception.region.RegionNotFoundException;
import com.aemreunal.repository.beacon.BeaconRepo;
import com.aemreunal.repository.beacon.BeaconSpecs;

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
    @SuppressWarnings("SpringJavaAutowiringInspection")
    private RegionService regionService;

    @Autowired
    @SuppressWarnings("SpringJavaAutowiringInspection")
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
            throws ConstraintViolationException, BeaconAlreadyExistsException {
        if (GlobalSettings.DEBUGGING) {
            System.out.println("Saving beacon with ID = \'" + beacon.getBeaconId() + "\'");
        }
        Region region = regionService.getRegion(username, projectId, regionId);
        if (beacon.getRegion() == null) {
            // This means it hasn't been created yet
            if (beaconExists(username, projectId, regionId, beacon)) {
                // First, verify the beacon doesn't already exist
                throw new BeaconAlreadyExistsException(beacon);
            } else if (!region.beaconCoordsAreValid(beacon)) {
                // Check whether the beacon coordinates are valid
                throw new MalformedRequestException();
            }
            beacon.setRegion(region);
        }
        return beaconRepo.save(beacon);
    }

    private boolean beaconExists(String username, Long projectId, Long regionId, Beacon beacon) {
        List<Beacon> beacons;
        try {
            beacons = findBeaconsBySpecs(username, projectId, regionId, beacon.getUuid(), beacon.getMajor(), beacon.getMinor());
        } catch (BeaconNotFoundException e) {
            return false;
        }
        return beacons.size() != 0;
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
     *
     * @return The list of beacons conforming to given constraints
     */
    @Transactional(readOnly = true)
    public List<Beacon> findBeaconsBySpecs(String username, Long projectId, Long regionId, String uuid, Integer major, Integer minor)
            throws BeaconNotFoundException {
        if (GlobalSettings.DEBUGGING) {
            System.out.println("Finding beacons with UUID = \'" + uuid + "\' major = \'" + major + "\' minor = \'" + minor + "\'");
        }
        List<Beacon> beacons = beaconRepo.findAll(BeaconSpecs.beaconWithSpecification(username, projectId, regionId, uuid, major, minor));
        if (beacons.size() == 0) {
            throw new BeaconNotFoundException();
        }
        return beacons;
    }

    @Transactional(readOnly = true)
    public Beacon queryForBeacon(String uuid, Integer major, Integer minor, String projectSecret)
    throws BeaconNotFoundException {
        List<Beacon> beacons = beaconRepo.findAll(BeaconSpecs.beaconWithSpecification(null, null, null, uuid, major, minor));
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
    public Beacon getBeacon(String username, Long projectId, Long regionId, Long beaconId) throws BeaconNotFoundException, ProjectNotFoundException, RegionNotFoundException {
        if (GlobalSettings.DEBUGGING) {
            System.out.println("Finding beacon with ID = \'" + beaconId + "\' in project = \'" + projectId + "\' and in region = \'" + regionId + "\'");
        }
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
    public List<Beacon> getBeaconsOfRegion(String username, Long projectId, Long regionId) {
        Region region = regionService.getRegion(username, projectId, regionId);
        return region.getBeacons().stream().collect(Collectors.toList());
    }

    public void setBeaconScenario(String username, Long projectId, Long regionId, Beacon beacon, Scenario scenario) {
        beacon.setScenario(scenario);
        save(username, projectId, regionId, beacon);
    }

    public Beacon delete(String username, Long projectId, Long regionId, Long beaconId) {
        if (GlobalSettings.DEBUGGING) {
            System.out.println("Deleting beacon with ID = \'" + beaconId + "\'");
        }
        // Retrieving beacon to ensure that beacon exists and is part of this user/project/region etc.
        Beacon beacon = getBeacon(username, projectId, regionId, beaconId);
        beaconRepo.delete(beaconId);
        return beacon;
    }
}
