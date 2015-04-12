package com.aemreunal.controller.region;

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

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;
import com.aemreunal.config.GlobalSettings;
import com.aemreunal.domain.Region;
import com.aemreunal.exception.imageStorage.ImageLoadException;
import com.aemreunal.exception.imageStorage.ImageSaveException;
import com.aemreunal.exception.region.MultipartFileReadException;
import com.aemreunal.exception.region.WrongFileTypeSubmittedException;
import com.aemreunal.service.RegionService;

@Controller
@RequestMapping(GlobalSettings.REGION_PATH_MAPPING)
public class RegionController {
    @Autowired
    private RegionService regionService;

    /**
     * Get regions that belong to a project.
     *
     * @param username
     *         The username of the owner of the region.
     * @param projectId
     *         The ID of the project the region belongs to.
     *
     * @return The list of regions that belong to the project.
     */
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Region>> getRegionsOfProject(@PathVariable String username,
                                                            @PathVariable Long projectId,
                                                            @RequestParam(value = "name", required = false, defaultValue = "") String regionName) {
        if (regionName.equals("")) {
            List<Region> regions = regionService.getAllRegionsOf(username, projectId);
            return new ResponseEntity<List<Region>>(regions, HttpStatus.OK);
        } else {
            List<Region> regions = regionService.findRegionsBySpecs(username, projectId, regionName);
            return new ResponseEntity<List<Region>>(regions, HttpStatus.OK);
        }
    }

    /**
     * Get the region with specified ID.
     *
     * @param username
     *         The username of the owner of the region.
     * @param projectId
     *         The ID of the project the region belongs to.
     * @param regionId
     *         The ID of the region.
     *
     * @return The region.
     */
    @RequestMapping(method = RequestMethod.GET, value = GlobalSettings.REGION_ID_MAPPING, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Region> getRegion(@PathVariable String username,
                                            @PathVariable Long projectId,
                                            @PathVariable Long regionId) {
        Region region = regionService.getRegion(username, projectId, regionId);
        // TODO add links
        return new ResponseEntity<Region>(region, HttpStatus.OK);
    }

    /**
     * Create a new region in project. The [REST] method accepts the region JSON and the
     * region image file as Multipart files. The region JSON Multipart file must be named
     * {@code region} and the region image Multipart file must be named {@code image}.
     * <p>
     * For the image file, the method accepts {@link org.springframework.http.MediaType#IMAGE_JPEG
     * JPEG}, {@link org.springframework.http.MediaType#IMAGE_PNG PNG}, and {@link
     * org.springframework.http.MediaType#IMAGE_GIF GIF} images. The image size must be <=
     * {@link com.aemreunal.config.GlobalSettings#MAX_UPLOAD_SIZE_BYTES
     * MAX_UPLOAD_SIZE_BYTES} Bytes.
     *
     * @param username
     *         The username of the owner of the project.
     * @param projectId
     *         The ID of the project the region belongs to.
     * @param region
     *         The region object to be created.
     * @param imageMultipartFile
     *         The region map image file as a {@link org.springframework.web.multipart.MultipartFile
     *         MultipartFile}.
     * @param builder
     *         The URI builder for post-creation redirect.
     *
     * @return The created region as JSON.
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
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Region> createRegion(@PathVariable String username,
                                               @PathVariable Long projectId,
                                               @RequestPart(value = "region") Region region,
                                               @RequestPart(value = "image") MultipartFile imageMultipartFile,
                                               UriComponentsBuilder builder)
            throws WrongFileTypeSubmittedException, ImageSaveException, MultipartFileReadException {
        Region savedRegion = regionService.saveNewRegion(username, projectId, region, imageMultipartFile);
        GlobalSettings.log("Saved region with ID = \'" + savedRegion.getRegionId() +
                                   "\' name = \'" + savedRegion.getName() +
                                   "\' in project with ID = \'" + projectId + "\'");
        return buildCreateResponse(username, builder, savedRegion);
    }

    private ResponseEntity<Region> buildCreateResponse(String username, UriComponentsBuilder builder, Region savedRegion) {
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(builder.path(GlobalSettings.REGION_SPECIFIC_MAPPING)
                                   .buildAndExpand(
                                           username,
                                           savedRegion.getProject().getProjectId(),
                                           savedRegion.getRegionId())
                                   .toUri());
        return new ResponseEntity<Region>(savedRegion, headers, HttpStatus.CREATED);
    }

    /**
     * Download the region map image. The image is sent as a byte stream.
     *
     * @param username
     *         The username of the owner of the region.
     * @param projectId
     *         The ID of the project the region belongs to.
     * @param regionId
     *         The ID of the region.
     *
     * @return The region map image as {@code byte[]}.
     *
     * @throws ImageLoadException
     *         If the map image couldn't be loaded from the filesystem.
     */
    @RequestMapping(method = RequestMethod.GET, value = GlobalSettings.REGION_MAP_IMAGE_MAPPING, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    // When RegionNotFoundException is triggered as a result of this method, an HttpMediaTypeNotAcceptableException
    // exception is raised. It's not raised if the exception handler also returns ResponseEntity<byte[]>.
    // TODO Fix.
    public ResponseEntity<byte[]> downloadRegionMapImage(@PathVariable String username,
                                                         @PathVariable Long projectId,
                                                         @PathVariable Long regionId)
            throws ImageLoadException {
        return new ResponseEntity<byte[]>(regionService.getMapImage(username, projectId, regionId), HttpStatus.OK);
    }

    /**
     * Delete the specified region.
     *
     * @param username
     *         The username of the owner of the region.
     * @param projectId
     *         The ID of the project the region belongs to.
     * @param regionId
     *         The ID of the region.
     *
     * @return The deleted region.
     */
    @RequestMapping(method = RequestMethod.DELETE, value = GlobalSettings.REGION_ID_MAPPING, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Region> deleteRegion(@PathVariable String username,
                                               @PathVariable Long projectId,
                                               @PathVariable Long regionId,
                                               @RequestParam(value = "confirm", required = true) String confirmation) {

        if (confirmation.toLowerCase().equals("yes")) {
            Region deletedRegion = regionService.delete(username, projectId, regionId);
            return new ResponseEntity<Region>(deletedRegion, HttpStatus.OK);
        } else {
            return new ResponseEntity<Region>(HttpStatus.PRECONDITION_FAILED);
        }
    }
}
