package com.aemreunal.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.aemreunal.config.GlobalSettings;
import com.aemreunal.controller.DeleteResponse;
import com.aemreunal.domain.User;
import com.aemreunal.exception.user.InvalidUsernameException;
import com.aemreunal.exception.user.UsernameClashException;
import com.aemreunal.repository.user.UserRepo;
import com.aemreunal.repository.user.UserSpecs;

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

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    /**
     * Saves/updates the given user
     *
     * @param user
     *     The user to save/update
     *
     * @return The saved/updated user
     */
    public User save(User user) {
        // Encrypt the password if a new user is persisted.
        // TODO check possible password re-hashing bug when user is updated
        if (user.getUserId() == null) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        verifyUsernameCorrectness(user.getUsername());
        verifyUsernameUniqueness(user.getUsername());

        if (GlobalSettings.DEBUGGING) {
            System.out.println("Saving user with ID = \'" + user.getUserId() + "\'");
        }
        return userRepo.save(user);
    }

    /**
     * Checks whether the specified username is correct (whether it already exists,
     * whether it contains spaces or not, etc.).
     * <p/>
     * Usernames must: <li>Be less than {@value com.aemreunal.domain.User#USERNAME_MAX_LENGTH}
     * characters</li> <li>Start with a letter</li> <li>Not have any spaces</li> <li>Not
     * have any non-ASCII characters</li> <li>Not be the same as any existing
     * username</li>
     *
     * @param username
     *     The username to check
     *
     * @throws InvalidUsernameException
     *     When the username is invalid due to reasons stated above
     */
    private void verifyUsernameCorrectness(String username) throws InvalidUsernameException {
        if (username.length() > User.USERNAME_MAX_LENGTH) {
            // The specified username contains more characters than allowed
            throw new InvalidUsernameException(username, "Username contains more than allowed number of characters!");
        } else if (!Character.isLetter(username.charAt(0))) {
            // The specified username does not begin with a letter
            throw new InvalidUsernameException(username, "Username does not begin with a letter!");
        } else if (username.indexOf(' ') != -1) {
            // The specified username contains spaces
            throw new InvalidUsernameException(username, "Username can not contain spaces!");
        } else if (username.matches(".*[^\\p{ASCII}].*")) {
            // The specified username contains non-ASCII characters
            throw new InvalidUsernameException(username, "Username can not contain non-ASCII characters!");
        } else {
            for (char ch : username.toCharArray()) {
                if (!Character.isLetterOrDigit(ch)) {
                    // The specified username contains a non-alphanumeric character
                    throw new InvalidUsernameException(username, "Username contains an illegal (non-alphanumeric) character!");
                }
            }
        }
    }

    /**
     * Check whether the given username is already taken or not
     *
     * @param username
     *     The username of the user to check
     *
     * @throws UsernameClashException
     *     When the username already exists
     */
    public void verifyUsernameUniqueness(String username) throws UsernameClashException {
        if (GlobalSettings.DEBUGGING) {
            System.out.println("Checking whether username = \'" + username + "\' is taken");
        }
        if (userRepo.count(UserSpecs.usernameSpecification(username)) != 0) {
            // The specified username already exists
            throw new UsernameClashException(username);
        }
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
        verifyUsernameCorrectness(username);
        // TODO throw not found ?
        return userRepo.findByUsername(username);
    }

    /**
     * Checks whether the specified user exists and if so, authenticates this user. If the
     * authentication fails either because the user is not found or the password is
     * incorrect, 'null' will be returned.
     *
     * @param username
     *     The username of the user to authenticate
     * @param rawPassword
     *     The raw password of the user to authenticate
     *
     * @return If authentication is successful, the user is returned
     */
    // TODO Should we specify whether the username or password was wrong or not? Would it cause a possible security breach?
    public User authenticateAndFindUser(String username, String rawPassword) {
        User user = findByUsername(username);
        if (user == null) {
            return null;
        }
        if (passwordEncoder.matches(rawPassword, user.getPassword())) {
            return user;
        } else {
            return null;
        }
    }

//    /**
//     * Deletes the user with the given ID and deletes everything (projects, etc.)
//     * associated with the user
//     *
//     * @param id
//     *     The ID of the user to delete
//     *
//     * @return Whether the user was deleted or not
//     */
//    public DeleteResponse delete(Long id) {
//        if (GlobalSettings.DEBUGGING) {
//            System.out.println("Deleting user with ID = \'" + id + "\'");
//        }
//        if (userRepo.exists(id)) {
//            userRepo.delete(id);
//            return DeleteResponse.DELETED;
//        } else {
//            return DeleteResponse.NOT_FOUND;
//        }
//    }

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
        User userToDelete = findByUsername(username);
        if (userToDelete != null) {
            userRepo.delete(userToDelete);
            return DeleteResponse.DELETED;
        } else {
            return DeleteResponse.NOT_FOUND;
        }
    }
}
