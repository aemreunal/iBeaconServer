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
import com.aemreunal.exception.connection.ConnectionExistsException;
import com.aemreunal.exception.connection.ConnectionNotFoundException;
import com.aemreunal.exception.imageStorage.ImageDeleteException;
import com.aemreunal.exception.imageStorage.ImageLoadException;
import com.aemreunal.exception.imageStorage.ImageSaveException;
import com.aemreunal.exception.region.*;
import com.aemreunal.helper.ImageProperties;
import com.aemreunal.helper.ImageStorage;
import com.aemreunal.repository.connection.ConnectionRepo;
import com.aemreunal.repository.connection.ConnectionSpecs;

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
    throws ConnectionExistsException, WrongFileTypeSubmittedException, ImageDeleteException, MultipartFileReadException, ImageSaveException {
        // Check whether such a connection already exists
        checkConnectionExistence(username, projectId, beaconOneId, regionOneId, beaconTwoId, regionTwoId);
        GlobalSettings.log("Creating new connection for user: \'" + username + "\' and project: \'" + projectId + "\', between beacons: \'" + beaconOneId + "\' & " + beaconTwoId);
        // Save image
        ImageProperties imageProperties = saveConnectionImage(username, projectId, imageMultipartFile);
        // Create connection
        Project project = projectService.getProject(username, projectId);
        Connection connection = createConnection(project, imageProperties);
        return connectBeacons(username, projectId, beaconOneId, regionOneId, beaconTwoId, regionTwoId, connection);
    }

    @Transactional(readOnly = true)
    private void checkConnectionExistence(String username, Long projectId, Long beaconOneId, Long regionOneId, Long beaconTwoId, Long regionTwoId)
    throws ConnectionExistsException {
        try {
            this.getConnectionBetween(username, projectId, beaconOneId, regionOneId, beaconTwoId, regionTwoId);
        } catch (ConnectionNotFoundException e) {
            return;
        }
        throw new ConnectionExistsException(beaconOneId, beaconTwoId);
    }

    private ImageProperties saveConnectionImage(String username, Long projectId, MultipartFile imageMultipartFile)
    throws MultipartFileReadException, ImageDeleteException, ImageSaveException, WrongFileTypeSubmittedException {
        GlobalSettings.log("Setting connection image of newly-created connection.");
        return imageStorage.saveImage(username, projectId, null, imageMultipartFile);
    }

    private Connection createConnection(Project project, ImageProperties imageProperties) {
        Connection connection = new Connection();
        connection.setProject(project);
        connection.setConnectionImageFileName(imageProperties.getImageFileName());
        return this.save(connection);
    }

    private Connection connectBeacons(String username, Long projectId, Long beaconOneId, Long regionOneId, Long beaconTwoId, Long regionTwoId, Connection connection) {
        // Connect connection entity and its beacons
        Beacon beaconOne = beaconService.addConnection(username, projectId, regionOneId, beaconOneId, connection);
        connection.addBeacon(beaconOne);
        Beacon beaconTwo = beaconService.addConnection(username, projectId, regionTwoId, beaconTwoId, connection);
        connection.addBeacon(beaconTwo);
        return this.save(connection);
    }

    @Transactional(readOnly = true)
    public Connection getConnectionWithId(String username, Long projectId, Long connectionId)
    throws ConnectionNotFoundException {
        Project project = projectService.getProject(username, projectId);
        Connection connection = connectionRepo.findByConnectionIdAndProject(connectionId, project);
        if (connection == null) {
            throw new ConnectionNotFoundException();
        }
        return connection;
    }

    @Transactional(readOnly = true)
    public Connection getConnectionBetween(String username, Long projectId, Long beaconOneId, Long regionOneId, Long beaconTwoId, Long regionTwoId)
    throws ConnectionNotFoundException {
        Beacon beaconOne = beaconService.getBeacon(username, projectId, regionOneId, beaconOneId);
        Beacon beaconTwo = beaconService.getBeacon(username, projectId, regionTwoId, beaconTwoId);
        Object connectionObj = connectionRepo.findOne(ConnectionSpecs.connectionWithSpecification(projectId, beaconOne, beaconTwo));
        if (connectionObj == null) {
            throw new ConnectionNotFoundException();
        }
        return (Connection) connectionObj;
    }

    @Transactional(readOnly = true)
    public byte[] getConnectionImage(String username, Long projectId, Long regionOneId, Long beaconOneId, Long regionTwoId, Long beaconTwoId)
    throws ConnectionNotFoundException, ImageLoadException {
        GlobalSettings.log("Getting connection image between beacons with ID = \'" + beaconOneId + "\' and \'" + beaconTwoId + "\'");
        Connection connection = this.getConnectionBetween(username, projectId, regionOneId, beaconOneId, regionTwoId, beaconTwoId);
        String connectionImageFileName = connection.getConnectionImageFileName();
        return imageStorage.loadImage(username, projectId, null, connectionImageFileName);
    }

    public void deleteConnection(String username, Long projectId, Long connectionId)
    throws ImageDeleteException {
        Project project = projectService.getProject(username, projectId);
        Connection connection = connectionRepo.findByConnectionIdAndProject(connectionId, project);
        imageStorage.deleteImage(username, projectId, null, connection.getConnectionImageFileName());
        connectionRepo.delete(connection);
    }
}
