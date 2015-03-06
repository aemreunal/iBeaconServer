package com.aemreunal.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.aemreunal.config.GlobalSettings;
import com.aemreunal.domain.Beacon;
import com.aemreunal.domain.Project;
import com.aemreunal.domain.Region;
import com.aemreunal.exception.region.*;
import com.aemreunal.helper.ImageStorage;
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
    private ImageStorage imageStorage;

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
        Project project = projectService.getProject(username, projectId);
        if (region.getProject() == null) {
            // Region is created
            region.setProject(project);
        } else {
            // Region is only updated
            region.markAsUpdated();
        }
        return regionRepo.save(region);
    }

    @Transactional(readOnly = true)
    public List<Region> getAllRegionsOf(String username, Long projectId) {
        Project project = projectService.getProject(username, projectId);
        return project.getRegions().stream().collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Region getRegion(String username, Long projectId, Long regionId) {
        if (GlobalSettings.DEBUGGING) {
            System.out.println("Finding region with ID = \'" + regionId + "\'");
        }
        Project project = projectService.getProject(username, projectId);
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
    @Transactional(readOnly = true)
    public List<Region> findRegionsBySpecs(String username, Long projectId, String regionName) {
        if (GlobalSettings.DEBUGGING) {
            System.out.println("Finding regions with projectID = \'" + projectId + "\' and name =\'" + regionName + "\'");
        }
        Project project = projectService.getProject(username, projectId);
        List<Region> regions = regionRepo.findAll(RegionSpecs.regionWithSpecification(project.getProjectId(), regionName));
        if (regions.size() == 0) {
            throw new RegionNotFoundException();
        }
        return regions;
    }

    @Transactional(readOnly = true)
    public List<Beacon> getMembersOfRegion(String username, Long projectId, Long regionId) {
        Region region = this.getRegion(username, projectId, regionId);
        return region.getBeacons().stream().collect(Collectors.toList());
    }

    public Region designateBeacon(String username, Long projectId, Long regionId, Long beaconId) {
        Region region = getRegion(username, projectId, regionId);
        Beacon beacon = beaconService.getBeacon(username, projectId, regionId, beaconId);
        region.designateBeacon(beacon);
        region.markAsUpdated();
        save(username, projectId, region);
        return region;
    }

    /**
     * Saves the map image (provided with the {@code mapImageInBytes} parameter) to disk
     * and saves the name of the image file to the region object. If the region already
     * has an image, it is overwritten.
     *
     * @param username
     *         The username of the owner of the project.
     * @param projectId
     *         The ID of the project to which the region belongs.
     * @param regionId
     *         The ID of the region to save the image of.
     * @param imageFile
     *         The image file as a {@link org.springframework.web.multipart.MultipartFile
     *         MultipartFile}.
     *
     * @return The updated region object.
     */
    public Region setMapImage(String username, Long projectId, Long regionId, MultipartFile imageFile)
            throws MapImageSaveException, MultipartFileReadException, MapImageDeleteException, WrongFileTypeSubmittedException {
        if (GlobalSettings.DEBUGGING) {
            System.out.println("Setting map image of region with ID = \'" + regionId + "\'");
        }
        if (imageFile.isEmpty() || !fileTypeIsImage(imageFile)) {
            throw new WrongFileTypeSubmittedException(projectId, regionId);
        }
        Region region = this.getRegion(username, projectId, regionId);
        String savedImageName = imageStorage.saveImage(username, projectId, regionId, region.getMapImageFileName(), imageFile);
        region.setMapImageFileName(savedImageName);
        // Save marks the region as updated
        save(username, projectId, region);
        return region;
    }

    private boolean fileTypeIsImage(MultipartFile file) {
        String type = file.getContentType();
        return type.equalsIgnoreCase(MediaType.IMAGE_JPEG_VALUE) ||
                type.equalsIgnoreCase(MediaType.IMAGE_PNG_VALUE) ||
                type.equalsIgnoreCase(MediaType.IMAGE_GIF_VALUE);
    }

    @Transactional(readOnly = true)
    public byte[] getMapImage(String username, Long projectId, Long regionId) throws MapImageLoadException, MapImageNotSetException {
        if (GlobalSettings.DEBUGGING) {
            System.out.println("Getting map image of region with ID = \'" + regionId + "\'");
        }
        Region region = this.getRegion(username, projectId, regionId);
        String mapImageFileName = region.getMapImageFileName();
        if (mapImageFileName == null) {
            throw new MapImageNotSetException(projectId, regionId);
        }
        byte[] mapImage = imageStorage.loadImage(username, projectId, regionId, mapImageFileName);
        if (mapImage == null) {
            throw new MapImageLoadException(projectId, regionId);
        }
        return mapImage;
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
        removeAllBeaconsFromRegion(username, projectId, region);
        removeRegionImage(username, projectId, region);
        regionRepo.delete(region);
        return region;
    }

    private void removeRegionImage(String username, Long projectId, Region region) {
        try {
            imageStorage.deleteImage(username, projectId, region.getRegionId(), region.getMapImageFileName());
        } catch (MapImageDeleteException e) {
            System.err.println("WARNING: Image file for user: " + username + ", project: "
                                       + projectId + ", region " + region.getRegionId() + ", file name: "
                                       + region.getMapImageFileName() + " could not be deleted! " +
                                       "May need to be deleted manually!");
        }
    }

    private void removeAllBeaconsFromRegion(String username, Long projectId, Region region) {
        for (Beacon beacon : region.getBeacons()) {
            beacon.setRegion(null);
            beaconService.save(username, projectId, region.getRegionId(), beacon);
        }
    }
}
