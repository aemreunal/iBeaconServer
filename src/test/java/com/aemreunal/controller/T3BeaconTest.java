package com.aemreunal.controller;

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

import java.util.UUID;
import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import com.aemreunal.domain.beacon.BeaconCreator;
import com.aemreunal.domain.beacon.BeaconGetter;
import com.aemreunal.domain.beacon.BeaconInfo;
import com.aemreunal.domain.beacon.BeaconRemover;
import com.aemreunal.domain.project.ProjectCreator;
import com.aemreunal.domain.project.ProjectInfo;
import com.aemreunal.domain.user.UserCreator;
import com.aemreunal.domain.user.UserInfo;

public class T3BeaconTest {
    private UserInfo testUser;
    private ProjectInfo testProject;

    @Before
    public void createTestUser() {
        testUser = UserCreator.createRandomUser();
        testProject = ProjectCreator.createRandomProject(testUser.username);
    }

    @Test
    public void createRandomBeacon() {
        BeaconCreator.createRandomBeacon(testUser.username, testProject.projectId);
        BeaconCreator.createRandomBeacon(testUser.username, testProject.projectId);
    }

    @Test
    public void getBeacon() {
        BeaconInfo createdBeacon1 = BeaconCreator.createRandomBeacon(testUser.username, testProject.projectId);
        BeaconInfo createdBeacon2 = BeaconCreator.createRandomBeacon(testUser.username, testProject.projectId);
        BeaconInfo requestedBeacon1 = BeaconGetter.getBeacon(testUser.username, testProject.projectId, createdBeacon1.beaconId);
        BeaconInfo requestedBeacon2 = BeaconGetter.getBeacon(testUser.username, testProject.projectId, createdBeacon2.beaconId);
        Assert.assertEquals("The created and requested beacons don't match!", createdBeacon1, requestedBeacon1);
        Assert.assertEquals("The created and requested beacons don't match!", createdBeacon2, requestedBeacon2);
        Assert.assertNotEquals("Beacons that are supposed to be different are the same!", createdBeacon1, requestedBeacon2);
        Assert.assertNotEquals("Beacons that are supposed to be different are the same!", createdBeacon2, requestedBeacon1);
    }

    @Test
    public void deleteBeacon() {
        BeaconInfo createdBeacon1 = BeaconCreator.createRandomBeacon(testUser.username, testProject.projectId);
        BeaconInfo createdBeacon2 = BeaconCreator.createRandomBeacon(testUser.username, testProject.projectId);
        BeaconRemover.removeBeacon(testUser.username, testProject.projectId, createdBeacon1.beaconId);
        BeaconGetter.failToGetBeacon(testUser.username, testProject.projectId, createdBeacon1.beaconId);
        BeaconGetter.getBeacon(testUser.username, testProject.projectId, createdBeacon2.beaconId);
        BeaconRemover.removeBeacon(testUser.username, testProject.projectId, createdBeacon2.beaconId);
        BeaconGetter.failToGetBeacon(testUser.username, testProject.projectId, createdBeacon1.beaconId);
        BeaconGetter.failToGetBeacon(testUser.username, testProject.projectId, createdBeacon2.beaconId);
    }

    @Test
    public void failToCreateBeacon() {
        BeaconCreator.failToCreateBeacon(testUser.username, testProject.projectId, "hello", "1234", "1234", "1234", HttpStatus.SC_BAD_REQUEST);
        BeaconCreator.failToCreateBeacon(testUser.username, testProject.projectId, UUID.randomUUID().toString(), "1234567", "1234", "1234", HttpStatus.SC_BAD_REQUEST);
        BeaconCreator.failToCreateBeacon(testUser.username, testProject.projectId, UUID.randomUUID().toString(), "1234", "1234567", "1234", HttpStatus.SC_BAD_REQUEST);
        BeaconCreator.failToCreateBeacon("hello world", testProject.projectId, UUID.randomUUID().toString(), "1234", "1234", "1234", HttpStatus.SC_BAD_REQUEST);
        BeaconCreator.failToCreateBeacon("helloworldasdasdas", testProject.projectId, UUID.randomUUID().toString(), "1234", "1234", "1234", HttpStatus.SC_NOT_FOUND);
        BeaconCreator.failToCreateBeacon(testUser.username, 1234L, UUID.randomUUID().toString(), "1234", "1234", "1234", HttpStatus.SC_NOT_FOUND);
    }
}
