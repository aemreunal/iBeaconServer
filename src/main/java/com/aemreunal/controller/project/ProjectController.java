package com.aemreunal.controller.project;

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
import com.aemreunal.controller.beacon.BeaconController;
import com.aemreunal.controller.BeaconGroupController;
import com.aemreunal.controller.user.UserController;
import com.aemreunal.domain.Project;
import com.aemreunal.service.ProjectService;
import com.aemreunal.service.UserService;

import static org.springframework.hateoas.core.DummyInvocationUtils.methodOn;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

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
@RequestMapping(GlobalSettings.PROJECT_PATH_MAPPING)
public class ProjectController {
    @Autowired
    private UserService userService;

    @Autowired
    private ProjectService projectService;

    /**
     * Get all projects of the user. Optionally the user may search their projects by
     * name
     *
     * @param username
     *     The username of the owner of the projects
     * @param projectName
     *     (Optional) The name of the project
     *
     * @return All existing projects (Optionally, all that match the given criteria)
     */
    @RequestMapping(method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public ResponseEntity<List<Project>> getAllProjects(
        @PathVariable String username,
        @RequestParam(value = "name", required = false, defaultValue = "") String projectName) {
        if (projectName.equals("")) {
            return new ResponseEntity<List<Project>>(projectService.findAllProjectsOf(username), HttpStatus.OK);
        } else {
            return getProjectsWithMatchingCriteria(username, projectName);
        }
    }

    /**
     * Returns the list of projects that match a given criteria
     *
     * @param username
     *     The username of the owner of the projects
     * @param projectName
     *     (Optional) The name of the project
     *
     * @return The list of projects that match the given criteria
     */
    private ResponseEntity<List<Project>> getProjectsWithMatchingCriteria(String username, String projectName) {
        List<Project> projects = projectService.findProjectsBySpecs(username, projectName);
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
    @RequestMapping(method = RequestMethod.GET, value = GlobalSettings.PROJECT_ID_MAPPING, produces = "application/json;charset=UTF-8")
    public ResponseEntity<Project> getProjectById(
        @PathVariable String username,
        @PathVariable Long projectId) {
        Project project = projectService.findById(username, projectId);
        // TODO add links
        return new ResponseEntity<Project>(addLinks(project), HttpStatus.OK);
    }

    /**
     * Creates a new project from the submitted JSON object in the request body.
     * <p/>
     * In the case of a constraint violation occurring during the save operation, a {@link
     * ConstraintViolationException} will be thrown from the {@link
     * com.aemreunal.service.ProjectService#save(String, com.aemreunal.domain.Project)
     * save()} method of {@link com.aemreunal.service.ProjectService}, propagated to this
     * method and then thrown from this one. This exception will be caught by the {@link
     * com.aemreunal.controller.project.ProjectControllerAdvice#constraintViolationExceptionHandler(javax.validation.ConstraintViolationException)
     * constraintViolationExceptionHandler()} of the {@link com.aemreunal.controller.project.ProjectControllerAdvice
     * ProjectControllerAdvice} class.
     *
     * @param projectFromJson
     *     The project as JSON object
     * @param builder
     *     The URI builder for post-creation redirect
     *
     * @return The created project
     *
     * @throws ConstraintViolationException
     */
    @RequestMapping(method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public ResponseEntity<JSONObject> createProject(@PathVariable String username,
                                                    @RequestBody Project projectFromJson,
                                                    UriComponentsBuilder builder)
        throws ConstraintViolationException {
        Project savedProject = projectService.save(username, projectFromJson);
        if (GlobalSettings.DEBUGGING) {
            System.out.println("Saved project with Name = \'" + savedProject.getName() + "\' ID = \'" + savedProject.getProjectId() + "\'");
        }
        String projectSecret = projectService.resetSecret(username, savedProject);
        return buildCreateResponse(builder, addLinks(savedProject), projectSecret);
    }

    /**
     * Builds the post-create response for a project. Sets the appropriate headers and
     * HATEOAS links and creates a {@link org.springframework.http.ResponseEntity
     * ResponseEntity} object with {@link net.minidev.json.JSONObject JSONObject} type.
     *
     * @param builder
     *     The URI builder for post-creation redirect
     * @param savedProject
     *     The project to create the response for
     * @param projectSecret
     *     The secret of the project
     *
     * @return The {@link org.springframework.http.ResponseEntity ResponseEntity} for this
     * project
     */
    private ResponseEntity<JSONObject> buildCreateResponse(UriComponentsBuilder builder, Project savedProject, String projectSecret) {
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(builder.path(GlobalSettings.PROJECT_SPECIFIC_MAPPING)
                                   .buildAndExpand(
                                       savedProject.getOwner().getUsername(),
                                       savedProject.getProjectId().toString())
                                   .toUri());
        return new ResponseEntity<JSONObject>(savedProject.getCreateResponse(projectSecret), headers, HttpStatus.CREATED);
    }

    /**
     * Adds the HATEOAS links to the {@link com.aemreunal.domain.Project Project} object.
     *
     * @param project
     *     Project to add the links to
     *
     * @return The project with links added
     *
     * @see <a href="https://github.com/spring-projects/spring-hateoas#link-builder">Spring
     * HATEOAS GitHub repo</a>
     */
    private Project addLinks(Project project) {
        String username = project.getOwner().getUsername();
        Long projectId = project.getProjectId();
        project.getLinks().add(linkTo(methodOn(ProjectController.class).getProjectById(username, projectId)).withSelfRel());
        project.getLinks().add(linkTo(methodOn(BeaconController.class).viewBeaconsOfProject(username, projectId, "", "", "")).withRel("beacons"));
        project.getLinks().add(linkTo(methodOn(BeaconGroupController.class).viewBeaconGroupsOfProject(username, projectId, "")).withRel("groups"));
        project.getLinks().add(linkTo(methodOn(UserController.class).getUserByUsername(username)).withRel("owner"));
        return project;
    }

    /**
     * Delete the specified project, along with all the beacons, beacon groups and
     * scenarios in the project.
     * <p/>
     * To delete the project, confirmation must be supplied as a URI parameter, in the
     * form of "?confirm=yes". If not supplied, the project will not be deleted.
     *
     * @param username
     *     The username of the owner of the project to delete
     * @param projectId
     *     The ID of the project to delete
     * @param confirmation
     *     The confirmation parameter
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.DELETE, value = GlobalSettings.PROJECT_ID_MAPPING)
    public ResponseEntity<Project> deleteProject(@PathVariable String username,
                                                 @PathVariable Long projectId,
                                                 @RequestParam(value = "confirm", required = true) String confirmation) {
        if (confirmation.toLowerCase().equals("yes")) {
            projectService.delete(username, projectId);
            return new ResponseEntity<Project>(HttpStatus.OK);
        } else {
            return new ResponseEntity<Project>(HttpStatus.PRECONDITION_FAILED);
        }
    }
}
