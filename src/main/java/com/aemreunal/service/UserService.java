package com.aemreunal.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.aemreunal.config.GlobalSettings;
import com.aemreunal.controller.DeleteResponse;
import com.aemreunal.domain.User;
import com.aemreunal.repository.user.UserRepo;

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

@Transactional
@Service
public class UserService {
    @Autowired
    private UserRepo userRepo;

    /**
     * Saves/updates the given user
     *
     * @param user
     *     The user to save/update
     *
     * @return The saved/updated user
     */
    public User save(User user) {
        if (GlobalSettings.DEBUGGING) {
            System.out.println("Saving user with ID = \'" + user.getUserId() + "\'");
        }

        return userRepo.save(user);
    }

    /**
     * Finds the user with the given ID
     *
     * @param id
     *     The ID of the user to search for
     *
     * @return The user the given ID
     */
    public User findById(Long id) {
        if (GlobalSettings.DEBUGGING) {
            System.out.println("Finding user with ID = \'" + id + "\'");
        }

        return userRepo.findOne(id);
    }

    /**
     * Find the user with the given username
     *
     * @param username
     *     The username of the user to search for
     *
     * @return The user with the given username
     */
    public User findByUsername(String username) {
        if (GlobalSettings.DEBUGGING) {
            System.out.println("Finding user with username = \'" + username + "\'");
        }

        return userRepo.findByUsername(username);
    }

    /**
     * Deletes the user with the given ID and deletes everything (projects, etc.)
     * associated with the user
     *
     * @param id
     *     The ID of the user to delete
     *
     * @return Whether the user was deleted or not
     */
    public DeleteResponse delete(Long id) {
        if (GlobalSettings.DEBUGGING) {
            System.out.println("Deleting user with ID = \'" + id + "\'");
        }
        if (userRepo.exists(id)) {
            userRepo.delete(id);
            return DeleteResponse.DELETED;
        } else {
            return DeleteResponse.NOT_FOUND;
        }
    }

    /**
     * Deletes the user with the given username and deletes everything (projects, etc.)
     * associated with the user
     *
     * @param username
     *     The username of the user to delete
     *
     * @return Whether the user was deleted or not
     */
    public DeleteResponse delete(String username) {
        if (GlobalSettings.DEBUGGING) {
            System.out.println("Deleting user with username = \'" + username + "\'");
        }
        User userToDelete = userRepo.findByUsername(username);
        if (userToDelete != null) {
            userRepo.delete(userToDelete);
            return DeleteResponse.DELETED;
        } else {
            return DeleteResponse.NOT_FOUND;
        }
    }
}
