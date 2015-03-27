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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.aemreunal.config.GlobalSettings;
import com.aemreunal.domain.Beacon;
import com.aemreunal.domain.Connection;
import com.aemreunal.domain.Project;
import com.aemreunal.exception.region.ImageDeleteException;
import com.aemreunal.exception.region.MapImageSaveException;
import com.aemreunal.exception.region.MultipartFileReadException;
import com.aemreunal.exception.region.WrongFileTypeSubmittedException;
import com.aemreunal.helper.ImageProperties;
import com.aemreunal.helper.ImageStorage;
import com.aemreunal.repository.connection.ConnectionRepo;

@Transactional
@Service
public class ConnectionService {
    @Autowired
    private ConnectionRepo connectionRepo;

    @Autowired
    private BeaconService beaconService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ImageStorage imageStorage;

    private Connection save(Connection connection) {
        return connectionRepo.save(connection);
    }

    public Connection createNewConnection(String username, Long projectId, Long beaconOneId, Long regionOneId, Long beaconTwoId, Long regionTwoId, MultipartFile imageMultipartFile)
    throws MapImageSaveException, ImageDeleteException, MultipartFileReadException, WrongFileTypeSubmittedException {
        GlobalSettings.log("Creating new connection for user: \'" + username + "\' and project: \'" + projectId
                                   + "\', between beacons: \'" + beaconOneId + "\' & " + beaconTwoId);
        Project project = projectService.getProject(username, projectId);

        ImageProperties imageProperties = saveConnectionImage(username, projectId, imageMultipartFile);

        Connection connection = createConnection(project, imageProperties);

        // Connect connection and its beacons
        Beacon beaconOne = beaconService.addConnection(username, projectId, regionOneId, beaconOneId, connection);
        connection.addBeacon(beaconOne);
        Beacon beaconTwo = beaconService.addConnection(username, projectId, regionTwoId, beaconTwoId, connection);
        connection.addBeacon(beaconTwo);

        return this.save(connection);
    }

    private Connection createConnection(Project project, ImageProperties imageProperties) {
        Connection connection = new Connection();
        connection.setProject(project);
        connection.setConnectionImageFileName(imageProperties.getImageFileName());
        return this.save(connection);
    }

    private ImageProperties saveConnectionImage(String username, Long projectId, MultipartFile imageMultipartFile)
    throws MultipartFileReadException, ImageDeleteException, MapImageSaveException, WrongFileTypeSubmittedException {
        GlobalSettings.log("Setting connection image of newly-created connection.");
        return imageStorage.saveImage(username, projectId, null, imageMultipartFile);
    }

    @Transactional(readOnly = true)
    public Connection getConnection(String username, Long projectId, Long connectionId) {
        Project project = projectService.getProject(username, projectId);
        return connectionRepo.findByConnectionIdAndProject(connectionId, project);
    }

    public void deleteConnection(String username, Long projectId, Long connectionId)
    throws ImageDeleteException {
        Project project = projectService.getProject(username, projectId);
        Connection connection = connectionRepo.findByConnectionIdAndProject(connectionId, project);
        imageStorage.deleteImage(username, projectId, null, connection.getConnectionImageFileName());
        connectionRepo.delete(connection);
    }
}
