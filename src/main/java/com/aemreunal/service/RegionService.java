package com.aemreunal.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.aemreunal.config.GlobalSettings;
import com.aemreunal.domain.Beacon;
import com.aemreunal.domain.Region;
import com.aemreunal.domain.Project;
import com.aemreunal.domain.Scenario;
import com.aemreunal.exception.region.BeaconDoesNotHaveRegionException;
import com.aemreunal.exception.region.BeaconHasRegionException;
import com.aemreunal.exception.region.RegionNotFoundException;
import com.aemreunal.repository.region.RegionRepo;
import com.aemreunal.repository.region.RegionSpecs;

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
public class RegionService {

    @Autowired
    private RegionRepo regionRepo;

    @Autowired
    private BeaconService beaconService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ScenarioService scenarioService;

    /**
     * Saves/updates the given region
     *
     * @param region
     *         The region to save/update
     *
     * @return The saved/updated region
     */
    public Region save(String username, Long projectId, Region region) {
        if (GlobalSettings.DEBUGGING) {
            System.out.println("Saving region with ID = \'" + region.getRegionId() + "\'");
        }
        Project project = projectService.findProjectById(username, projectId);
        if (region.getProject() == null) {
            region.setProject(project);
        }
        return regionRepo.save(region);
    }

    public List<Region> getAllRegionsOf(String username, Long projectId) {
        Project project = projectService.findProjectById(username, projectId);
        List<Region> regions = new ArrayList<Region>();
        for (Region region : project.getRegions()) {
            regions.add(region);
        }
        return regions;
    }

    public Region getRegion(String username, Long projectId, Long regionId) {
        if (GlobalSettings.DEBUGGING) {
            System.out.println("Finding region with ID = \'" + regionId + "\'");
        }
        Project project = projectService.findProjectById(username, projectId);
        Region region = regionRepo.findByRegionIdAndProject(regionId, project);
        if (region == null) {
            throw new RegionNotFoundException(regionId);
        }
        return region;
    }

    /**
     * Finds the regions conforming to given specifications
     *
     * @param projectId
     *         The project ID constraint
     * @param regionName
     *         The name field constraint
     *
     * @return The list of regions conforming to given constraints
     */
    public List<Region> findRegionsBySpecs(String username, Long projectId, String regionName) {
        if (GlobalSettings.DEBUGGING) {
            System.out.println("Finding regions with projectID = \'" + projectId + "\' and name =\'" + regionName + "\'");
        }
        Project project = projectService.findProjectById(username, projectId);
        List<Region> regions = regionRepo.findAll(RegionSpecs.regionWithSpecification(project.getProjectId(), regionName));
        if (regions.size() == 0) {
            throw new RegionNotFoundException();
        }
        return regions;
    }

    public List<Beacon> getMembersOfRegion(String username, Long projectId, Long regionId) {
        Region region = this.getRegion(username, projectId, regionId);
        List<Beacon> beaconList = region.getBeacons().stream().collect(Collectors.toList());
        return beaconList;
    }

    public Region addBeaconToRegion(String username, Long projectId, Long regionId, Long beaconId) {
        Region region = getRegion(username, projectId, regionId);
        Beacon beacon = beaconService.getBeacon(username, projectId, beaconId);
        // Check pre-existing region
        Region currentRegion = beacon.getRegion();
        if (currentRegion != null) {
            throw new BeaconHasRegionException(beaconId, currentRegion.getRegionId());
        }
        // Check pre-existing scenario
        Scenario currentScenario = beacon.getScenario();
        if (currentScenario != null) {
            scenarioService.removeBeaconFromScenario(username, projectId, currentScenario.getScenarioId(), beaconId);
        }
        beacon.setRegion(region);
        beaconService.save(username, projectId, beacon);
        return region;
    }

    public Region removeBeaconFromRegion(String username, Long projectId, Long regionId, Long beaconId) {
        Region region = getRegion(username, projectId, regionId);
        Beacon beacon = beaconService.getBeacon(username, projectId, beaconId);
        checkIfBeaconBelongsToRegion(regionId, beacon);
        beacon.setRegion(null);
        beaconService.save(username, projectId, beacon);
        return region;
    }

    public Region designateBeacon(String username, Long projectId, Long regionId, Long beaconId) {
        Region region = getRegion(username, projectId, regionId);
        Beacon beacon = beaconService.getBeacon(username, projectId, beaconId);
        checkIfBeaconBelongsToRegion(regionId, beacon);
        region.designateBeacon(beacon);
        save(username, projectId, region);
        return region;
    }

    /**
     * Checks the relationship between a beacon and a region. The relationship is
     * valid if the beacon belongs to that region; in that case, no exception is thrown. If
     * the beacon doesn't belong to any region, a {@link com.aemreunal.exception.region.BeaconDoesNotHaveRegionException
     * BeaconDoesNotHaveRegionException} is thrown. If the beacon belongs to another region,
     * a BeaconHasRegionException {@link com.aemreunal.exception.region.BeaconHasRegionException
     * BeaconHasRegionException} is thrown.
     *
     * @param regionId
     *         The ID of the region to check the relationship of
     * @param beacon
     *         The beacon object to check the relationship of
     *
     * @throws com.aemreunal.exception.region.BeaconDoesNotHaveRegionException
     *         If the beacon doesn't belong to any region
     * @throws com.aemreunal.exception.region.BeaconHasRegionException
     *         If the beacon belongs to another region, different from the one designated
     *         with the parameter {@code regionId}
     */
    private void checkIfBeaconBelongsToRegion(Long regionId, Beacon beacon)
            throws BeaconDoesNotHaveRegionException, BeaconHasRegionException {
        // Check region
        Region currentRegion = beacon.getRegion();
        if (currentRegion == null) {
            // Valid if the beacon doesn't have a region
            throw new BeaconDoesNotHaveRegionException(beacon.getBeaconId(), regionId);
        } else if (!(currentRegion.getRegionId().equals(regionId))) {
            // Valid if the beacon belongs to another region
            throw new BeaconHasRegionException(beacon.getBeaconId(), beacon.getRegion().getRegionId());
        }
    }

    /**
     * Deletes the region with the given ID and updates the beacons in the region.
     *
     * @param projectId
     *         The ID of the project to delete the beacon from
     * @param regionId
     *         The ID of the region to delete
     *
     * @return Whether the region was deleted or not
     */
    public Region delete(String username, Long projectId, Long regionId) {
        if (GlobalSettings.DEBUGGING) {
            System.out.println("Deleting region with ID = \'" + regionId + "\'");
        }
        Region region = this.getRegion(username, projectId, regionId);
        removeBeaconsFromRegion(region, username, projectId);
        regionRepo.delete(region);
        return region;
    }

    private void removeBeaconsFromRegion(Region region, String username, Long projectId) {
        for (Beacon beacon : region.getBeacons()) {
            beacon.setRegion(null);
            beaconService.save(username, projectId, beacon);
        }
    }
}
