package com.aemreunal.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.aemreunal.config.GlobalSettings;
import com.aemreunal.controller.DeleteResponse;
import com.aemreunal.domain.Project;
import com.aemreunal.domain.User;
import com.aemreunal.exception.project.ProjectNotFoundException;
import com.aemreunal.repository.project.ProjectRepo;
import com.aemreunal.repository.project.ProjectSpecs;

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

@Transactional
@Service
public class ProjectService {
    @Autowired
    private UserService userService;

    @Autowired
    private ProjectRepo projectRepo;

    @Autowired
    private BCryptPasswordEncoder encoder;

    /**
     * Saves/updates the given project
     *
     * @param project
     *     The project to save/update
     *
     * @return The saved/updated project
     */
    public Project save(Project project) throws ConstraintViolationException {
        if (GlobalSettings.DEBUGGING) {
            System.out.println("Saving project with ID = \'" + project.getProjectId() + "\'");
        }
        return projectRepo.save(project);
    }

    /**
     * Resets the project secret.
     * <p/>
     * Generates a new secret UUID, encodes it with BCrypt and stores it encoded but
     * returns the plaintext, so that it can be shown to the user.
     *
     * @return The plain-text project secret
     */
    public String resetSecret(String username, Long projectId) throws ProjectNotFoundException {
        String secret = UUID.randomUUID().toString().toUpperCase();
        Project project = this.findById(username, projectId);
        if (project == null) {
            throw new ProjectNotFoundException();
        }
        project.setProjectSecret(encoder.encode(secret));
        this.save(project);
        return secret;
    }

    /**
     * Returns the list of all the projects. Will only work in debug mode. Otherwise, will
     * only return an empty list.
     *
     * @return A list of all projects
     */
    public List<Project> findAll() {
        if (GlobalSettings.DEBUGGING) {
            System.out.println("Finding all projects");
        }

        List<Project> projectList = new ArrayList<Project>();

// TODO        if (GlobalSettings.DEBUGGING) {
        for (Project project : projectRepo.findAll()) {
            projectList.add(project);
        }
//        }

        return projectList;
    }

    /**
     * Find the projects that belong to the specific {@link com.aemreunal.domain.User
     * User} with the specified username. Searches through the projects, matching the
     * {@link com.aemreunal.domain.User User} with the specified username.
     *
     * @param ownerUsername
     *     The username of the {@link com.aemreunal.domain.User User} to find the projects
     *     of
     *
     * @return The list of projects that belong to the {@link com.aemreunal.domain.User
     * User} with the specified username
     */
    public List<Project> findAllBelongingTo(String ownerUsername) {
        User owner = userService.findByUsername(ownerUsername);
        return this.findAllBelongingTo(owner);
    }

    /**
     * Find the projects that belong to a specific {@link com.aemreunal.domain.User User}.
     * Searches through the projects, matching the specified {@link
     * com.aemreunal.domain.User User}.
     *
     * @param owner
     *     The {@link com.aemreunal.domain.User User} to find the projects of
     *
     * @return The list of projects that belong to the {@link com.aemreunal.domain.User
     * User}
     */
    public List<Project> findAllBelongingTo(User owner) {
        // TODO or, find the user and .getProjects()?
        List<Project> projectList = new ArrayList<Project>();
        for (Project project : projectRepo.findByOwner(owner)) {
            projectList.add(project);
        }
        return projectList;
    }

    /**
     * Find projects conforming to specifications
     *
     * @param username
     *     The username of the owner of the projects to search for
     * @param projectName
     *     The project Name field constraint
     *
     * @return The list of projects conforming to given constraints
     */
    public List<Project> findProjectsBySpecs(String username, String projectName) {
        if (GlobalSettings.DEBUGGING) {
            System.out.println("Finding projects with Project Name = \'" + projectName + "\'");
        }

        List<Project> projectList = new ArrayList<Project>();
        User owner = userService.findByUsername(username);
        for (Object projectObject : projectRepo.findAll(ProjectSpecs.projectWithSpecification(owner, projectName))) {
            projectList.add((Project) projectObject);
        }

        return projectList;
    }

    /**
     * Finds the project with the given ID
     *
     * @param username
     *     The username of the owner of the project
     * @param id
     *     The ID of the project to search for
     *
     * @return The project the given ID
     */
    public Project findById(String username, Long id) {
        if (GlobalSettings.DEBUGGING) {
            System.out.println("Finding project with ID = \'" + id + "\'");
        }
        // TODO handle 'user not found' as an exception
        User owner = userService.findByUsername(username);
        return projectRepo.findByOwnerAndProjectId(owner, id);
    }

    /**
     * Deletes the project with the given ID and deletes the beacons and beacon groups in
     * the project
     *
     * @param id
     *     The ID of the project to delete
     *
     * @return Whether the project was deleted or not
     */
    public DeleteResponse delete(Long id) {
        if (GlobalSettings.DEBUGGING) {
            System.out.println("Deleting project with ID = \'" + id + "\'");
        }
        if (projectRepo.exists(id)) {
            projectRepo.delete(id);
            return DeleteResponse.DELETED;
        } else {
            return DeleteResponse.NOT_FOUND;
        }
    }
}
