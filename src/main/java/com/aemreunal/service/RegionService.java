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

import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.aemreunal.config.GlobalSettings;
import com.aemreunal.domain.Beacon;
import com.aemreunal.domain.Project;
import com.aemreunal.domain.Region;
import com.aemreunal.exception.imageStorage.ImageDeleteException;
import com.aemreunal.exception.imageStorage.ImageLoadException;
import com.aemreunal.exception.imageStorage.ImageSaveException;
import com.aemreunal.exception.region.MultipartFileReadException;
import com.aemreunal.exception.region.RegionNotFoundException;
import com.aemreunal.exception.region.WrongFileTypeSubmittedException;
import com.aemreunal.helper.ImageProperties;
import com.aemreunal.helper.ImageStorage;
import com.aemreunal.repository.region.RegionRepo;
import com.aemreunal.repository.region.RegionSpecs;

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
     * Saves/updates the given region. If the region does not exist in the database,
     * persists it in the database.
     *
     * @param username
     *         The username of the owner of the project.
     * @param projectId
     *         The ID of the project which the region resides in.
     * @param region
     *         The region to be saved/updated.
     *
     * @return The saved/updated region.
     */
    private Region save(String username, Long projectId, Region region) {
        GlobalSettings.log("Saving region with ID = \'" + region.getRegionId() + "\'");
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

    /**
     * Creates and saves the newly created region, along with its image.
     *
     * @param username
     *         The username of the owner of the project.
     * @param projectId
     *         The ID of the project which the region resides in.
     * @param region
     *         The region to be created and saved.
     * @param imageMultipartFile
     *         The image of the region as a {@link org.springframework.web.multipart.MultipartFile
     *         MultipartFile}.
     *
     * @return The saved region.
     *
     * @throws WrongFileTypeSubmittedException
     *         The file type of the {@link org.springframework.web.multipart.MultipartFile
     *         MultipartFile} file submitted as the region map image is of some other type
     *         than JPEG, GIF, or PNG.
     * @throws ImageSaveException
     *         If the server is unable to save the region map image.
     * @throws MultipartFileReadException
     *         If the Multipart file couldn't be read.
     */
    public Region saveNewRegion(String username, Long projectId, Region region, MultipartFile imageMultipartFile)
            throws WrongFileTypeSubmittedException, ImageSaveException, MultipartFileReadException {
        GlobalSettings.log("Creating new region for user = \'" + username + "\' and project = \'" + projectId + "\'");
        // Region must be saved prior to setting the map image, as the map
        // image storage in filesystem depends on region and project IDs.
        region = this.save(username, projectId, region);
        return setMapImage(username, projectId, region, imageMultipartFile);
    }

    /**
     * Saves the map image (provided with the {@code mapImageInBytes} parameter) to disk
     * and sets the name of the image file to the region object. If the region already has
     * an image, it is overwritten. The region is NOT saved.
     *
     * @param username
     *         The username of the owner of the project.
     * @param projectId
     *         The ID of the project to which the region belongs.
     * @param region
     *         The region to save the image of.
     * @param imageFile
     *         The image file as a {@link org.springframework.web.multipart.MultipartFile
     *         MultipartFile}.
     *
     * @return The updated region object.
     *
     * @throws WrongFileTypeSubmittedException
     *         The file type of the {@link org.springframework.web.multipart.MultipartFile
     *         MultipartFile} file submitted as the region map image is of some other type
     *         than JPEG, GIF, or PNG.
     * @throws ImageSaveException
     *         If the server is unable to save the region map image.
     * @throws MultipartFileReadException
     *         If the Multipart file couldn't be read.
     */
    private Region setMapImage(String username, Long projectId, Region region, MultipartFile imageFile)
            throws ImageSaveException, MultipartFileReadException, WrongFileTypeSubmittedException {
        GlobalSettings.log("Setting map image of region with ID = \'" + region.getRegionId() + "\'");
        ImageProperties savedImageProperties = imageStorage.saveImage(username, projectId, region.getRegionId(), imageFile);
        region.setImageProperties(savedImageProperties);
        return this.save(username, projectId, region);
    }

    @Transactional(readOnly = true)
    public List<Region> getAllRegionsOf(String username, Long projectId) {
        Project project = projectService.getProject(username, projectId);
        return project.getRegions().stream().collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Region getRegion(String username, Long projectId, Long regionId) {
        GlobalSettings.log("Finding region with ID = \'" + regionId + "\'");
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
        GlobalSettings.log("Finding regions with projectID = \'" + projectId + "\' and name =\'" + regionName + "\'");
        Project project = projectService.getProject(username, projectId);
        List<Region> regions = regionRepo.findAll(RegionSpecs.regionWithSpecification(project.getProjectId(), regionName));
        if (regions.size() == 0) {
            throw new RegionNotFoundException();
        }
        return regions;
    }

    @Transactional(readOnly = true)
    public LinkedHashSet<Beacon> getMembersOfRegion(String username, Long projectId, Long regionId) {
        Region region = this.getRegion(username, projectId, regionId);
        return region.getBeacons().stream()
                     .sorted()
                     .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Transactional(readOnly = true)
    public byte[] getMapImage(String username, Long projectId, Long regionId)
    throws ImageLoadException {
        GlobalSettings.log("Getting map image of region with ID = \'" + regionId + "\'");
        Region region = this.getRegion(username, projectId, regionId);
        String mapImageFileName = region.getMapImageFileName();
        return imageStorage.loadImage(username, projectId, regionId, mapImageFileName);
    }

    public Region markRegionAsUpdated(String username, Long projectId, Long regionId) {
        Region region = this.getRegion(username, projectId, regionId);
        region.markAsUpdated();
        return regionRepo.save(region);
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
        GlobalSettings.log("Deleting region with ID = \'" + regionId + "\'");
        Region region = this.getRegion(username, projectId, regionId);
        removeRegionImage(username, projectId, region);
        regionRepo.delete(region);
        return region;
    }

    private void removeRegionImage(String username, Long projectId, Region region) {
        try {
            imageStorage.deleteImage(username, projectId, region.getRegionId(), region.getMapImageFileName());
        } catch (ImageDeleteException e) {
            System.err.println("WARNING: Image file for user: " + username + ", project: "
                                       + projectId + ", region " + region.getRegionId() + ", file name: "
                                       + region.getMapImageFileName() + " could not be deleted! " +
                                       "May need to be deleted manually!");
        }
    }
}
