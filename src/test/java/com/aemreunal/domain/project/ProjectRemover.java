package com.aemreunal.domain.project;

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

public class ProjectRemover extends EntityRemover {
    public static void removeProject(String username, Long projectId) {
        removeEntity("/" + username + "/projects/" + projectId);
    }

    public static void failToRemoveProject(String username, Long projectId) {
        sendDeleteRequest("/" + username + "/projects/" + projectId + "?confirm=yes", HttpStatus.SC_NOT_FOUND);
    }
}
