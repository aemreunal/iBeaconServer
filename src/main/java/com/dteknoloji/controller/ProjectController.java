package com.dteknoloji.controller;

import java.util.List;
import javax.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import com.dteknoloji.config.GlobalSettings;
import com.dteknoloji.domain.Beacon;
import com.dteknoloji.domain.BeaconGroup;
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
        if (ownerID.equals("")) {
            ownerIDAsLong = null;
        } else {
            try {
                ownerIDAsLong = Long.valueOf(ownerID);
            } catch (NumberFormatException e) {
                return new ResponseEntity<List<Project>>(HttpStatus.BAD_REQUEST);
            }
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
        }
        return new ResponseEntity<Project>(project, HttpStatus.OK);
    }

    /**
     * Create a new project
     *
     * @param project
     *     The project as JSON object
     * @param builder
     *     The URI builder for post-creation redirect
     *
     * @return The created project
     */
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Project> createProject(@RequestBody Project project, UriComponentsBuilder builder) {
        try {
            Project newProject = projectService.save(project);
            if (GlobalSettings.DEBUGGING) {
                System.out.println("Saved project with Name = \'" + newProject.getName() + "\' ID = \'" + newProject.getProjectId() + "\'");
            }
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(builder.path("/Project/{id}").buildAndExpand(newProject.getProjectId().toString()).toUri());
            return new ResponseEntity<Project>(newProject, headers, HttpStatus.CREATED);
        } catch (ConstraintViolationException | TransactionSystemException e) {
            if (GlobalSettings.DEBUGGING) {
                System.err.println("Unable to save project! Constraint violation detected!");
            }
            return new ResponseEntity<Project>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Create a new beacon in project
     * <p/>
     * {@literal @}Transactional mark via http://stackoverflow.com/questions/11812432/spring-data-hibernate
     *
     * @param id
     *     The ID of the project to create the beacon in
     * @param restBeacon
     *     The beacon as JSON object
     * @param builder
     *     The URI builder for post-creation redirect
     *
     * @return The created beacon
     */
    @Transactional
    @RequestMapping(method = RequestMethod.POST, value = "/{id}/CreateBeacon", produces = "application/json")
    public ResponseEntity<Beacon> createBeaconInProject(@PathVariable String id, @RequestBody Beacon restBeacon, UriComponentsBuilder builder) {
        Long projectIDAsLong;
        try {
            projectIDAsLong = Long.valueOf(id);
        } catch (NumberFormatException e) {
            return new ResponseEntity<Beacon>(HttpStatus.BAD_REQUEST);
        }

        Project project = projectService.findById(projectIDAsLong);
        if (project == null) {
            return new ResponseEntity<Beacon>(HttpStatus.NOT_FOUND);
        }

        restBeacon.setProject(project);

        Beacon newBeacon;
        try {
            newBeacon = beaconService.save(restBeacon);
        } catch (ConstraintViolationException | TransactionSystemException e) {
            if (GlobalSettings.DEBUGGING) {
                System.err.println("Unable to save beacon! Constraint violation detected!");
            }
            return new ResponseEntity<Beacon>(HttpStatus.BAD_REQUEST);
        }

        if (GlobalSettings.DEBUGGING) {
            System.out.println("Saved beacon with UUID = \'" + newBeacon.getUuid() +
                "\' major = \'" + newBeacon.getMajor() +
                "\' minor = \'" + newBeacon.getMinor() +
                "\' in project with ID = \'" + projectIDAsLong + "\'");
        }
        project.addBeacon(newBeacon);
        projectService.save(project);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(builder.path("/Beacon/{id}").buildAndExpand(newBeacon.getBeaconId().toString()).toUri());
        return new ResponseEntity<Beacon>(newBeacon, headers, HttpStatus.CREATED);
    }

    /**
     * Create a new beacon group in project
     * <p/>
     * {@literal @}Transactional mark via http://stackoverflow.com/questions/11812432/spring-data-hibernate
     *
     * @param id
     *     The ID of the project to create the beacon group in
     * @param restBeaconGroup
     *     The beacon group as JSON object
     * @param builder
     *     The URI builder for post-creation redirect
     *
     * @return The created beacon group
     */
    @Transactional
    @RequestMapping(method = RequestMethod.POST, value = "/{id}/CreateBeaconGroup", produces = "application/json")
    public ResponseEntity<BeaconGroup> createBeaconGroupInProject(@PathVariable String id, @RequestBody BeaconGroup restBeaconGroup, UriComponentsBuilder builder) {
        Long projectIDAsLong;
        try {
            projectIDAsLong = Long.valueOf(id);
        } catch (NumberFormatException e) {
            return new ResponseEntity<BeaconGroup>(HttpStatus.BAD_REQUEST);
        }

        Project project = projectService.findById(projectIDAsLong);
        if (project == null) {
            return new ResponseEntity<BeaconGroup>(HttpStatus.NOT_FOUND);
        }

        restBeaconGroup.setProject(project);

        BeaconGroup newBeaconGroup;
        try {
            newBeaconGroup = beaconGroupService.save(restBeaconGroup);
        } catch (ConstraintViolationException | TransactionSystemException e) {
            if (GlobalSettings.DEBUGGING) {
                System.err.println("Unable to save beacon group! Constraint violation detected!");
            }
            return new ResponseEntity<BeaconGroup>(HttpStatus.BAD_REQUEST);
        }

        if (GlobalSettings.DEBUGGING) {
            System.out.println("Saved beacon group with ID = \'" + newBeaconGroup.getBeaconGroupId() +
                "\' name = \'" + newBeaconGroup.getName() +
                "\' in project with ID = \'" + projectIDAsLong + "\'");
        }
        project.addBeaconGroup(newBeaconGroup);
        projectService.save(project);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(builder.path("/BeaconGroup/{id}").buildAndExpand(newBeaconGroup.getBeaconGroupId().toString()).toUri());
        return new ResponseEntity<BeaconGroup>(newBeaconGroup, headers, HttpStatus.CREATED);
    }

    /**
     * Delete the specified project
     *
     * @param id
     *     The ID of the project to delete
     *
     * @return The deleted project
     */
    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}", produces = "application/json")
    public ResponseEntity<Project> deleteProject(@PathVariable String id) {
        Long projectIDAsLong;
        try {
            projectIDAsLong = Long.valueOf(id);
        } catch (NumberFormatException e) {
            return new ResponseEntity<Project>(HttpStatus.BAD_REQUEST);
        }

        Project project = projectService.findById(projectIDAsLong);

        if (project == null) {
            return new ResponseEntity<Project>(HttpStatus.NOT_FOUND);
        }

        boolean deleted = projectService.delete(projectIDAsLong);
        if (deleted) {
            return new ResponseEntity<Project>(project, HttpStatus.OK);
        }

        return new ResponseEntity<Project>(HttpStatus.FORBIDDEN);
    }
}
