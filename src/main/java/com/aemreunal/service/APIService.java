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

import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.aemreunal.config.GlobalSettings;
import com.aemreunal.domain.Project;
import com.aemreunal.domain.Region;
import com.aemreunal.exception.imageStorage.ImageLoadException;
import com.aemreunal.exception.project.ProjectNotFoundException;
import com.aemreunal.helper.ImageStorage;
import com.aemreunal.repository.project.ProjectRepo;

@Transactional(readOnly = true)
@Service
public class APIService {
    @Autowired
    private ProjectRepo projectRepo;

    @Autowired
    private ImageStorage imageStorage;

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

    public byte[] queryForRegionImage(Long projectId, String projectSecret, Long regionId)
    throws ImageLoadException {
        Set<Region> regions = queryForRegionsOfProject(projectId, projectSecret);
        ArrayList<String> regionImageNameSet = regions.stream()
                                                      .filter(region -> region.getRegionId().equals(regionId))
                                                      .map(Region::getMapImageFileName)
                                                      .collect(Collectors.toCollection(ArrayList::new));
        if (regionImageNameSet.size() != 1) {
            return null;
        }
        String regionImageName = regionImageNameSet.get(0);
        return imageStorage.loadImage(projectId, regionId, regionImageName);
    }

    private static void initLazily(Object proxy) {
        if (!Hibernate.isInitialized(proxy)) {
            Hibernate.initialize(proxy);
        }
    }
}
