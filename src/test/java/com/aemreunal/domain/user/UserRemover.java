package com.aemreunal.domain.user;

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

import org.apache.http.HttpStatus;
import com.aemreunal.domain.EntityRemover;

public class UserRemover extends EntityRemover {
    public static void removeUser(String username) {
        removeEntity("/" + username);
    }

    public static void failToRemoveUser(String username) {
        sendDeleteRequest("/" + username + "?confirm=yes", HttpStatus.SC_NOT_FOUND);
    }
}
