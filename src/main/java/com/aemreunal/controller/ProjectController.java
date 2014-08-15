package com.aemreunal.controller;

import net.minidev.json.JSONObject;

import java.util.List;
import javax.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
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
@RequestMapping("/project")
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
    // TODO Fix project criteria search
    // TODO Fix project criteria search
    // TODO Fix project criteria search
    // TODO Fix project criteria search
    // TODO Fix project criteria search
    // TODO Fix project criteria search
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
    public ResponseEntity<Project> getProjectById(
        @PathVariable Long projectId/*,
        @RequestHeader(value = "Authorization") String projectSecret*/) {
        // TODO search with secret
        Project project = projectService.findById(projectId);
        if (project == null) {
            return new ResponseEntity<Project>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Project>(addLinks(project), HttpStatus.OK);
    }

    /**
     * Creates a new project from the submitted JSON object in the request body.
     * <p/>
     * In the case of a constraint violation occurring during the save operation, a {@link
     * ConstraintViolationException} will be thrown from the {@link
     * com.aemreunal.service.ProjectService#save(com.aemreunal.domain.Project) save()}
     * method of {@link com.aemreunal.service.ProjectService}, propagated to this method
     * and then thrown from this one. This exception will be caught by the {@link
     * com.aemreunal.controller.project.ProjectControllerAdvice#constraintViolationExceptionHandler(javax.validation.ConstraintViolationException)
     * constraintViolationExceptionHandler()} of the {@link com.aemreunal.controller.project.ProjectControllerAdvice
     * ProjectControllerAdvice} class.
     *
     * @param projectJson
     *     The project as JSON object
     * @param builder
     *     The URI builder for post-creation redirect
     *
     * @return The created project
     *
     * @throws ConstraintViolationException
     */
    @RequestMapping(method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public ResponseEntity<JSONObject> createProject(
        @RequestBody Project projectJson,
        UriComponentsBuilder builder)
        throws ConstraintViolationException {
        Project savedProject;
        savedProject = projectService.save(projectJson);
        if (GlobalSettings.DEBUGGING) {
            System.out.println("Saved project with Name = \'" + savedProject.getName() + "\' ID = \'" + savedProject.getProjectId() + "\'");
        }
        String projectSecret = projectService.resetSecret(savedProject.getProjectId());
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(builder.path("/project/{id}").buildAndExpand(savedProject.getProjectId().toString()).toUri());
        return new ResponseEntity<JSONObject>(savedProject.getCreateResponse(projectSecret), headers, HttpStatus.CREATED);
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
    /*
    Produces:
        "links": [
            {
              "rel": "self",
              "href": "http://localhost:8080/project/2"
            },
            {
              "rel": "beacons",
              "href": "http://localhost:8080/project/2/beacon?uuid=&major=&minor="
            },
            {
              "rel": "groups",
              "href": "http://localhost:8080/project/2/beacongroup?name="
            }
        ]
     */
    private Project addLinks(Project project) {
        project.getLinks().add(linkTo(methodOn(ProjectController.class).getProjectById(project.getProjectId()/*, "<Project secret>"*/)).withSelfRel());
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
    @RequestMapping(method = RequestMethod.DELETE, value = "/{projectId}")
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
