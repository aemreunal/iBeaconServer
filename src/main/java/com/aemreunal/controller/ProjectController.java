package com.aemreunal.controller;

import java.util.List;
import javax.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import com.aemreunal.config.GlobalSettings;
import com.aemreunal.domain.Project;
import com.aemreunal.service.ProjectService;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

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
    @RequestMapping(method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
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
    @RequestMapping(method = RequestMethod.GET, value = "/{projectId}", produces = "application/json;charset=UTF-8")
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
     * @param projectJson
     *     The project as JSON object
     * @param builder
     *     The URI builder for post-creation redirect
     *
     * @return The created project
     */
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Project> createProject(
        @RequestBody Project projectJson,
        UriComponentsBuilder builder) {
        Project savedProject;
        try {
            savedProject = projectService.save(projectJson);
            projectService.save(addLinks(savedProject));
        } catch (ConstraintViolationException | TransactionSystemException e) {
            if (GlobalSettings.DEBUGGING) {
                System.err.println("Unable to save project! Constraint violation detected!");
            }
            return new ResponseEntity<Project>(HttpStatus.BAD_REQUEST);
        }
        if (GlobalSettings.DEBUGGING) {
            System.out.println("Saved project with Name = \'" + savedProject.getName() + "\' ID = \'" + savedProject.getProjectId() + "\'");
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(builder.path("/Project/{id}").buildAndExpand(savedProject.getProjectId().toString()).toUri());
        return new ResponseEntity<Project>(savedProject, headers, HttpStatus.CREATED);
    }

    /**
     * Adds the HATEOAS links to the project object. Object must be saved afterwards to
     * ensure persistance of added links.
     *
     * @param project
     *     Project to add the links to
     *
     * @return The project with links added
     */
    // TODO https://github.com/spring-projects/spring-hateoas#link-builder
    private Project addLinks(Project project) {
        project.getLinks().add(linkTo(methodOn(ProjectController.class).viewProject(project.getProjectId())).withSelfRel());
        project.getLinks().add(linkTo(methodOn(BeaconController.class).viewBeaconsOfProject(project.getProjectId(), "", "", "")).withRel("beacons"));
        project.getLinks().add(linkTo(methodOn(BeaconGroupController.class).viewBeaconGroupsOfProject(project.getProjectId(), "")).withRel("groups"));
        return project;
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
