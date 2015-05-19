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

import java.util.Set;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.aemreunal.config.GlobalSettings;
import com.aemreunal.domain.Beacon;
import com.aemreunal.domain.Connection;
import com.aemreunal.domain.Project;
import com.aemreunal.domain.Region;
import com.aemreunal.exception.connection.ConnectionNotFoundException;
import com.aemreunal.exception.imageStorage.ImageLoadException;
import com.aemreunal.exception.project.ProjectNotFoundException;
import com.aemreunal.exception.region.RegionNotFoundException;
import com.aemreunal.exception.textStorage.TextLoadException;
import com.aemreunal.helper.ImageStorage;
import com.aemreunal.helper.TextStorage;
import com.aemreunal.repository.beacon.BeaconRepo;
import com.aemreunal.repository.connection.ConnectionRepo;
import com.aemreunal.repository.project.ProjectRepo;
import com.aemreunal.repository.region.RegionRepo;

@Transactional(readOnly = true)
@Service
public class APIService {
    @Autowired
    private ProjectRepo projectRepo;

    @Autowired
    private RegionRepo regionRepo;

    @Autowired
    private BeaconRepo beaconRepo;

    @Autowired
    private ConnectionRepo connectionRepo;

    @Autowired
    private ImageStorage imageStorage;

    @Autowired
    private TextStorage textStorage;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public Project queryForProject(Long projectId, String projectSecret)
    throws ProjectNotFoundException {
        GlobalSettings.log("Querying for project with ID = \'" + projectId + "\'");
        Project project = projectRepo.findOne(projectId);
        if (project != null) {
            if (passwordEncoder.matches(projectSecret, project.getProjectSecret())) {
                return project;
            }
        }
        throw new ProjectNotFoundException();
    }

    public Set<Region> queryForRegionsOfProject(Long projectId, String projectSecret)
    throws ProjectNotFoundException {
        Project project = queryForProject(projectId, projectSecret);
        Set<Region> regions = project.getRegions();
        initLazily(regions);
        return regions;
    }

    public Set<Connection> queryForConnections(Long projectId, String secret) {
        Project project = queryForProject(projectId, secret);
        Set<Connection> connections = project.getConnections();
        initLazily(connections);
        for (Connection connection : connections) {
            initLazily(connection.getBeacons());
        }
        return connections;
    }

    public Region queryForRegionOfProject(Long projectId, String projectSecret, Long regionId)
    throws RegionNotFoundException {
        Project project = queryForProject(projectId, projectSecret);
        Region region = regionRepo.findByRegionIdAndProject(regionId, project);
        if (region == null) {
            throw new RegionNotFoundException(regionId);
        }
        return region;
    }

    private Connection queryForConnectionOfProject(Long projectId, String projectSecret, Long connectionId)
    throws ConnectionNotFoundException {
        Project project = queryForProject(projectId, projectSecret);
        Connection connection = connectionRepo.findByConnectionIdAndProject(connectionId, project);
        if (connection == null) {
            throw new ConnectionNotFoundException();
        }
        return connection;
    }

    public Set<Beacon> queryForBeaconsOfRegion(Long projectId, String projectSecret, Long regionId)
    throws RegionNotFoundException {
        Region region = queryForRegionOfProject(projectId, projectSecret, regionId);
        Set<Beacon> beacons = region.getBeacons();
        initLazily(beacons);
        return beacons;
    }

    public byte[] queryForImageOfRegion(Long projectId, String projectSecret, Long regionId)
    throws RegionNotFoundException, ImageLoadException {
        Region region = queryForRegionOfProject(projectId, projectSecret, regionId);
        String regionImageName = region.getMapImageFileName();
        return imageStorage.loadImage(projectId, regionId, regionImageName);
    }

    public byte[] queryForImageOfConnection(Long projectId, String projectSecret, Long connectionId)
    throws ImageLoadException, ConnectionNotFoundException {
        Connection connection = queryForConnectionOfProject(projectId, projectSecret, connectionId);
        String connectionImageName = connection.getConnectionImageFileName();
        return imageStorage.loadImage(projectId, null, connectionImageName);
    }

    private static void initLazily(Object proxy) {
        if (!Hibernate.isInitialized(proxy)) {
            Hibernate.initialize(proxy);
        }
    }

    public String queryForLocationInfoOfBeacon(Long projectId, String secret, Long regionId, Long beaconId)
    throws TextLoadException {
        Region region = queryForRegionOfProject(projectId, secret, regionId);
        Beacon beacon = beaconRepo.findByBeaconIdAndRegion(beaconId, region);
        String text = textStorage.loadText(projectId, regionId, beaconId, beacon.getLocationInfoTextFileName());
        return text;
    }
}
