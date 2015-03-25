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
import java.util.List;
import java.util.UUID;
import javax.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.aemreunal.config.GlobalSettings;
import com.aemreunal.domain.Project;
import com.aemreunal.domain.User;
import com.aemreunal.exception.project.ProjectNotFoundException;
import com.aemreunal.repository.project.ProjectRepo;
import com.aemreunal.repository.project.ProjectSpecs;

@Transactional
@Service
public class ProjectService {
    @Autowired
    private UserService userService;

    @Autowired
    private ProjectRepo projectRepo;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    /**
     * Saves/updates the given project. The given username parameter is used to set the
     * owner of the project when the project is first created (saved/persisted).
     *
     * @param username
     *         The username of the owner of the project
     * @param project
     *         The project to save/update
     *
     * @return The saved/updated project
     */
    public Project save(String username, Project project) throws ConstraintViolationException {
        GlobalSettings.log("Saving project with ID = \'" + project.getProjectId() + "\'");
        if (project.getOwner() == null) {
            project.setOwner(userService.findByUsername(username));
        }
        return projectRepo.save(project);
    }

    /**
     * Resets the project secret.
     * <p>
     * Generates a new secret UUID, encodes it with BCrypt and stores it encoded but
     * returns the plaintext, so that it can be shown to the user.
     *
     * @return The plain-text project secret
     */
    public String resetSecret(String username, Project project) throws ProjectNotFoundException {
        String secret = UUID.randomUUID().toString().toUpperCase();
        project.setProjectSecret(passwordEncoder.encode(secret));
        this.save(username, project);
        return secret;
    }

    /**
     * Find the projects that belong to the specific {@link com.aemreunal.domain.User
     * User} with the specified username. Searches through the projects, matching the
     * {@link com.aemreunal.domain.User User} with the specified username.
     *
     * @param ownerUsername
     *         The username of the {@link com.aemreunal.domain.User User} to find the
     *         projects of
     *
     * @return The list of projects that belong to the {@link com.aemreunal.domain.User
     * User} with the specified username
     */
    @Transactional(readOnly = true)
    public List<Project> getAllProjectsOf(String ownerUsername) {
        User owner = userService.findByUsername(ownerUsername);
        List<Project> projectList = new ArrayList<Project>();
        for (Project project : owner.getProjects()) {
            projectList.add(project);
        }
        return projectList;
    }

    /**
     * Find projects conforming to specifications
     *
     * @param username
     *         The username of the owner of the projects to search for
     * @param projectName
     *         The project Name field constraint
     *
     * @return The list of projects conforming to given constraints
     */
    @Transactional(readOnly = true)
    public List<Project> findProjectsBySpecs(String username, String projectName) {
        GlobalSettings.log("Finding projects with Project Name = \'" + projectName + "\'");
        User owner = userService.findByUsername(username);
        List<Project> projects = projectRepo.findAll(ProjectSpecs.projectWithSpecification(owner, projectName));
        if (projects.size() == 0) {
            throw new ProjectNotFoundException();
        }

        return projects;
    }

    /**
     * Finds the project with the given ID
     *
     * @param username
     *         The username of the owner of the project
     * @param projectId
     *         The ID of the project to search for
     *
     * @return The project the given ID
     *
     * @throws ProjectNotFoundException
     */
    @Transactional(readOnly = true)
    public Project getProject(String username, Long projectId) throws ProjectNotFoundException {
        GlobalSettings.log("Finding project with ID = \'" + projectId + "\'");
        // Verify owner exists
        User owner = userService.findByUsername(username);
        Project project = projectRepo.findByOwnerAndProjectId(owner, projectId);
        if (project == null) {
            throw new ProjectNotFoundException();
        }
        return project;
    }

    @Transactional(readOnly = true)
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

    /**
     * Deletes the {@link com.aemreunal.domain.Project project} with the given ID and
     * deletes the {@link com.aemreunal.domain.Beacon beacons} and {@link
     * com.aemreunal.domain.Region regions} in the project.
     *
     * @param username
     *         The username of the owner of the project
     * @param projectId
     *         The ID of the project to delete
     *
     * @return The deleted project
     */
    public Project delete(String username, Long projectId) {
        GlobalSettings.log("Deleting project with ID = \'" + projectId + "\'");
        Project project = this.getProject(username, projectId);
        projectRepo.delete(project);
        return project;
    }
}
