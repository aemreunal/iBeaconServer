package com.aemreunal.controller;

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
import com.aemreunal.config.GlobalSettings;
import com.aemreunal.domain.BeaconGroup;
import com.aemreunal.domain.Project;
import com.aemreunal.service.BeaconGroupService;
import com.aemreunal.service.ProjectService;

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
    private BeaconGroupService beaconGroupService;

    /**
     * Get all projects (Optionally, all with matching criteria)
     *
     * @param projectName
     *     (Optional) The name of the project
     * @param ownerName
     *     (Optional) The name of the owner
     * @param ownerId
     *     (Optional) The ID of the owner
     *
     * @return All existing projects (Optionally, all that match the given criteria)
     */
    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<List<Project>> getAllProjects(
        @RequestParam(value = "name", required = false, defaultValue = "") String projectName,
        @RequestParam(value = "ownerName", required = false, defaultValue = "") String ownerName,
        @RequestParam(value = "ownerId", required = false, defaultValue = "") Long ownerId) {
        if (projectName.equals("") && ownerName.equals("") && ownerId == null) {
            return new ResponseEntity<List<Project>>(projectService.findAll(), HttpStatus.OK);
        } else {
            return getProjectsWithMatchingCriteria(projectName, ownerName, ownerId);
        }
    }

    /**
     * Returns the list of projects that match a given criteria
     *
     * @param projectName
     *     (Optional) The name of the project
     * @param ownerName
     *     (Optional) The name of the owner
     * @param ownerId
     *     (Optional) The ID of the owner
     *
     * @return The list of projects that match the given criteria
     */
    private ResponseEntity<List<Project>> getProjectsWithMatchingCriteria(String projectName, String ownerName, Long ownerId) {
        List<Project> projects = projectService.findProjectsBySpecs(projectName, ownerName, ownerId);
        if (projects.size() == 0) {
            return new ResponseEntity<List<Project>>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<List<Project>>(projects, HttpStatus.OK);
    }

    /**
     * Get the project with the specified ID
     *
     * @param projectId
     *     The ID of the project
     *
     * @return The project
     */
    @RequestMapping(method = RequestMethod.GET, value = "/{projectId}", produces = "application/json")
    public ResponseEntity<Project> viewProject(@PathVariable Long projectId) {
        Project project = projectService.findById(projectId);
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
    public ResponseEntity<Project> createProject(
        @RequestBody Project project,
        UriComponentsBuilder builder) {
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
     * Create a new beacon group in project
     * <p/>
     * {@literal @}Transactional mark via http://stackoverflow.com/questions/11812432/spring-data-hibernate
     *
     * @param projectId
     *     The ID of the project to create the beacon group in
     * @param restBeaconGroup
     *     The beacon group as JSON object
     * @param builder
     *     The URI builder for post-creation redirect
     *
     * @return The created beacon group
     */
    @Transactional
    @RequestMapping(method = RequestMethod.POST, value = "/{projectId}/BeaconGroup", produces = "application/json")
    public ResponseEntity<BeaconGroup> createBeaconGroupInProject(
        @PathVariable Long projectId,
        @RequestBody BeaconGroup restBeaconGroup,
        UriComponentsBuilder builder) {

        Project project = projectService.findById(projectId);
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
                "\' in project with ID = \'" + projectId + "\'");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(builder.path("/BeaconGroup/{id}").buildAndExpand(newBeaconGroup.getBeaconGroupId().toString()).toUri());
        return new ResponseEntity<BeaconGroup>(newBeaconGroup, headers, HttpStatus.CREATED);
    }

    /**
     * Get beacon groups that belong to a project.
     *
     * @param projectId
     *     The ID of the project
     *
     * @return The list of beacon groups that belong to the project with the specified ID
     */
    @RequestMapping(method = RequestMethod.GET, value = "/{projectId}/BeaconGroup", produces = "application/json")
    public ResponseEntity<List<BeaconGroup>> viewBeaconGroupsOfProject(@PathVariable Long projectId) {
        Project project = projectService.findById(projectId);
        if (project == null) {
            return new ResponseEntity<List<BeaconGroup>>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<List<BeaconGroup>>(project.getBeaconGroups(), HttpStatus.OK);
    }


    /**
     * Delete the specified project, along with all the beacons, beacon groups and
     * scenarios in the project.
     * <p/>
     * To delete the project, confirmation must be supplied as a URI parameter, in the
     * form of "?confirm=yes". If not supplied, the project will not be deleted.
     *
     * @param projectId
     *     The ID of the project to delete
     * @param confirmation
     *     The confirmation parameter
     *
     * @return The status of deletion action
     */
    @Transactional
    @RequestMapping(method = RequestMethod.DELETE, value = "/{projectId}", produces = "application/json")
    public ResponseEntity<Project> deleteProject(
        @PathVariable Long projectId,
        @RequestParam(value = "confirm", required = true) String confirmation) {

        DeleteResponse response = DeleteResponse.NOT_DELETED;
        if (confirmation.toLowerCase().equals("yes")) {
            response = projectService.delete(projectId);
        }

        switch (response) {
            case DELETED:
                return new ResponseEntity<Project>(HttpStatus.OK);
            case FORBIDDEN:
                return new ResponseEntity<Project>(HttpStatus.FORBIDDEN);
            case NOT_FOUND:
                return new ResponseEntity<Project>(HttpStatus.NOT_FOUND);
            case NOT_DELETED:
                return new ResponseEntity<Project>(HttpStatus.PRECONDITION_FAILED);
            default:
                return new ResponseEntity<Project>(HttpStatus.I_AM_A_TEAPOT);
        }
    }
}
