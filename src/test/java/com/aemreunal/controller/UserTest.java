package com.aemreunal.controller;

import org.junit.Test;
import com.aemreunal.domain.user.UserCreator;
import com.aemreunal.domain.user.UserGetter;
import com.aemreunal.domain.user.UserInfo;
import com.aemreunal.domain.user.UserRemover;

import static org.junit.Assert.assertTrue;

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

public class UserTest {
    @Test
    public void testCreateUser() {
        UserInfo userInfo = UserCreator.createRandomUser();
        System.out.println(userInfo);
    }

    @Test
    public void testGetUser() {
        UserInfo createdUserInfo = UserCreator.createRandomUser();
        // TODO add expected status code for 404 not found assertion post-delete
        UserInfo requestedUserInfo = UserGetter.getUser(createdUserInfo.username);
        assertTrue("The created and requested objects do not match!", createdUserInfo.equals(requestedUserInfo));
    }

    @Test
    public void testDeleteUser() {
        UserInfo createdUserInfo = UserCreator.createRandomUser();
        UserRemover.removeUser(createdUserInfo.username);
        UserGetter.failToGetUser(createdUserInfo.username);
    }
}
