package com.dteknoloji.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import com.dteknoloji.domain.Project;
import com.dteknoloji.service.BeaconGroupService;
import com.dteknoloji.service.BeaconService;
import com.dteknoloji.service.ProjectService;

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

@Controller
@RequestMapping("/Project")
public class ProjectController {
    @Autowired
    private ProjectService projectService;

    @Autowired
    private BeaconService beaconService;

    @Autowired
    private BeaconGroupService beaconGroupService;

    /**
     * Get all project (Optionally, all with matching criteria)
     *
     * @param projectName
     *     (Optional) The name of the project
     * @param ownerName
     *     (Optional) The name of the owner
     * @param ownerID
     *     (Optional) The ID of the owner
     *
     * @return All existing projects (Optionally, all that match the given criteria)
     */
    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<List<Project>> getAllProjects(
        @RequestParam(value = "name", required = false, defaultValue = "") String projectName,
        @RequestParam(value = "ownerName", required = false, defaultValue = "") String ownerName,
        @RequestParam(value = "ownerID", required = false, defaultValue = "") String ownerID) {
        if (projectName.equals("") && ownerName.equals("") && ownerID.equals("")) {
            return new ResponseEntity<List<Project>>(projectService.findAll(), HttpStatus.OK);
        } else {
            return getProjectsWithMatchingCriteria(projectName, ownerName, ownerID);
        }
    }

    /**
     * Returns the list of projects that match a given criteria
     *
     * @param projectName
     *     (Optional) The name of the project
     * @param ownerName
     *     (Optional) The name of the owner
     * @param ownerID
     *     (Optional) The ID of the owner
     *
     * @return The list of projects that match the given criteria
     */
    private ResponseEntity<List<Project>> getProjectsWithMatchingCriteria(String projectName, String ownerName, String ownerID) {
        Long ownerIDAsLong;
        try {
            ownerIDAsLong = Long.valueOf(ownerID);
        } catch (NumberFormatException e) {
            return new ResponseEntity<List<Project>>(HttpStatus.BAD_REQUEST);
        }

        List<Project> projects = projectService.findProjectsBySpecs(projectName, ownerName, ownerIDAsLong);
        if (projects.size() == 0) {
            return new ResponseEntity<List<Project>>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<List<Project>>(projects, HttpStatus.OK);
    }

    /**
     * Get the project with the specified ID
     *
     * @param id
     *     The ID of the project
     *
     * @return The project
     */
    @RequestMapping(method = RequestMethod.GET, value = "/{id}", produces = "application/json")
    public ResponseEntity<Project> viewProject(@PathVariable String id) {
        Long projectIDAsLong;
        try {
            projectIDAsLong = Long.valueOf(id);
        } catch (NumberFormatException e) {
            return new ResponseEntity<Project>(HttpStatus.BAD_REQUEST);
        }

        Project project = projectService.findById(projectIDAsLong);
        if (project == null) {
            return new ResponseEntity<Project>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<Project>(project, HttpStatus.OK);
        }
    }
}
