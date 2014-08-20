package com.aemreunal.controller;

import java.util.ArrayList;
import java.util.Random;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import com.aemreunal.domain.project.ProjectCreator;
import com.aemreunal.domain.project.ProjectGetter;
import com.aemreunal.domain.project.ProjectInfo;
import com.aemreunal.domain.project.ProjectRemover;
import com.aemreunal.domain.user.UserCreator;
import com.aemreunal.domain.user.UserInfo;
import com.aemreunal.domain.user.UserRemover;

import static org.junit.Assert.*;

/*
 ***************************
 * Copyright (c) 2014      *
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
 ***************************
 */

/*
 * https://code.google.com/p/rest-assured/
 *
 * Documentation: http://code.google.com/p/rest-assured/wiki/Usage
 */

public class T2ProjectTest {
    public static final int NUM_SEARCH_REPEAT = 10;
    private UserInfo testUser;
    private Random random;

    @Before
    public void createTestUser() {
        testUser = UserCreator.createRandomUser();
        random = new Random();
    }

    @Test
    public void createProject() {
        ProjectCreator.createRandomProject(testUser.username);
    }

    @Test
    public void getProject() {
        ProjectInfo createdProject = ProjectCreator.createRandomProject(testUser.username);
        ProjectInfo requestedProject = ProjectGetter.getProject(testUser.username, createdProject.projectId);
        assertEquals("The created and requested projects don't match!", createdProject, requestedProject);
    }

    @Test
    public void deleteProject() {
        ProjectInfo createdProject = ProjectCreator.createRandomProject(testUser.username);
        ProjectInfo requestedProject = ProjectGetter.getProject(testUser.username, createdProject.projectId);
        assertEquals("The created and requested projects don't match!", createdProject, requestedProject);
        ProjectRemover.removeProject(testUser.username, createdProject.projectId);
        ProjectGetter.failToGetProject(testUser.username, createdProject.projectId);
    }

    @Test
    public void createMultipleProjects1() {
        ProjectCreator.createRandomProject(testUser.username);
        ProjectCreator.createRandomProject(testUser.username);
        ProjectCreator.createRandomProject(testUser.username);
    }

    @Test
    public void createMultipleProjects2() {
        ProjectCreator.createRandomProject(testUser.username);
        ProjectCreator.createRandomProject(testUser.username);
        ProjectCreator.createRandomProject(testUser.username);
        UserInfo otherTestUser = UserCreator.createRandomUser();
        ProjectCreator.createRandomProject(otherTestUser.username);
        ProjectCreator.createRandomProject(otherTestUser.username);
        ProjectCreator.createRandomProject(otherTestUser.username);
    }

    @Test
    public void failToCreateProjectDueToUser() {
        ProjectCreator.failToCreateProjectWithBadUsername("nonExistantUser", "test", "test", true);
        ProjectCreator.failToCreateProjectWithBadUsername("Invalid username", "test", "test", false);
        ProjectCreator.failToCreateProjectWithBadUsername("Invalidüşernamé", "test", "test", false);
        ProjectCreator.failToCreateProjectWithBadUsername("user!", "test", "test", false);
    }

    @Test
    public void failToCreateInvalidProjects() {
        ProjectCreator.failToCreateProject(testUser.username, T1UserTest.LONG_NAME, "test", HttpStatus.SC_BAD_REQUEST);
        ProjectCreator.failToCreateProject(testUser.username, "test", T1UserTest.LONG_NAME + T1UserTest.LONG_NAME + T1UserTest.LONG_NAME + T1UserTest.LONG_NAME, HttpStatus.SC_BAD_REQUEST);
        ProjectCreator.failToCreateProject(testUser.username, T1UserTest.LONG_NAME, T1UserTest.LONG_NAME + T1UserTest.LONG_NAME + T1UserTest.LONG_NAME + T1UserTest.LONG_NAME, HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void failToCreateDeletedUserProject() {
        UserInfo otherTestUser = UserCreator.createRandomUser();
        ProjectCreator.createRandomProject(otherTestUser.username);
        ProjectCreator.createRandomProject(otherTestUser.username);
        UserRemover.removeUser(otherTestUser.username);
        ProjectCreator.failToCreateProjectWithBadUsername(otherTestUser.username, "test", "test", true);
        ProjectCreator.failToCreateProjectWithBadUsername(otherTestUser.username, "test", "test", true);
    }

    @Test
    public void getProjects() {
        ProjectInfo createdProject1 = ProjectCreator.createRandomProject(testUser.username);
        ProjectInfo createdProject2 = ProjectCreator.createRandomProject(testUser.username);
        ArrayList<ProjectInfo> projects = ProjectGetter.getAllProjects(testUser.username);
        assertTrue(projects.contains(createdProject1));
        assertTrue(projects.contains(createdProject2));
    }

    @Test
    public void failToGetProjects1() {
        ProjectInfo createdProject1 = ProjectCreator.createRandomProject(testUser.username);
        ProjectInfo createdProject2 = ProjectCreator.createRandomProject(testUser.username);
        ArrayList<ProjectInfo> projects = ProjectGetter.getAllProjects(testUser.username);
        assertTrue(projects.contains(createdProject1));
        assertTrue(projects.contains(createdProject2));
        // Remove project 1
        ProjectRemover.removeProject(createdProject1.ownerUsername, createdProject1.projectId);
        projects = ProjectGetter.getAllProjects(testUser.username);
        assertFalse(projects.contains(createdProject1));
        assertTrue(projects.contains(createdProject2));
        // Remove project 2
        ProjectRemover.removeProject(createdProject2.ownerUsername, createdProject2.projectId);
        projects = ProjectGetter.getAllProjects(testUser.username);
        assertFalse(projects.contains(createdProject1));
        assertFalse(projects.contains(createdProject2));
    }

    @Test
    public void failToGetProjects2() {
        UserInfo otherTestUser = UserCreator.createRandomUser();
        ProjectInfo createdProject1 = ProjectCreator.createRandomProject(otherTestUser.username);
        ProjectInfo createdProject2 = ProjectCreator.createRandomProject(otherTestUser.username);
        ArrayList<ProjectInfo> projects = ProjectGetter.getAllProjects(otherTestUser.username);
        assertTrue(projects.contains(createdProject1));
        assertTrue(projects.contains(createdProject2));
        // Remove user
        UserRemover.removeUser(otherTestUser.username);
        ProjectGetter.failToGetAllProjects(otherTestUser.username);
    }

    @Test
    public void searchProjectsExact() {
        ArrayList<ProjectInfo> projects;
        ProjectInfo createdProject1 = ProjectCreator.createRandomProject(testUser.username);
        ProjectInfo createdProject2 = ProjectCreator.createRandomProject(testUser.username);

        projects = ProjectGetter.searchForProjects(testUser.username, createdProject1.name);
        assertTrue("Search did not return created project with ID " + createdProject1.projectId + "!", projects.contains(createdProject1));
        projects = ProjectGetter.searchForProjects(testUser.username, createdProject2.name);
        assertTrue("Search did not return created project with ID " + createdProject2.projectId + "!", projects.contains(createdProject2));

        ProjectRemover.removeProject(testUser.username, createdProject1.projectId);
        ProjectGetter.failToSearchForProjects(testUser.username, createdProject1.name);

        ProjectRemover.removeProject(testUser.username, createdProject2.projectId);
        ProjectGetter.failToSearchForProjects(testUser.username, createdProject2.name);
    }

    @Test
    public void searchProjectsFuzzy() {
        ArrayList<ProjectInfo> projects;
        ProjectInfo createdProject1 = ProjectCreator.createRandomProject(testUser.username);
        ProjectInfo createdProject2 = ProjectCreator.createRandomProject(testUser.username);

        // Search 3 times to ensure
        for (int i = 0; i < NUM_SEARCH_REPEAT; i++) {
            projects = ProjectGetter.searchForProjects(testUser.username, getRandomSubstring(createdProject1.name));
            assertTrue(projects.contains(createdProject1));

            projects = ProjectGetter.searchForProjects(testUser.username, getRandomSubstring(createdProject2.name));
            assertTrue(projects.contains(createdProject2));
        }
    }

    public String getRandomSubstring(String str) {
        // +1 to include the last character of the String,
        // as the .substring already excludes it.
        int beginIndex = random.nextInt(str.length() + 1);
        int endIndex;
        do {
            endIndex = random.nextInt(str.length() + 1);
        } while (endIndex < beginIndex);
        return str.substring(beginIndex, endIndex);
    }
}
