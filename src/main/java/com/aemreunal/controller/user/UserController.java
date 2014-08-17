package com.aemreunal.controller.user;

import net.minidev.json.JSONObject;

import javax.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import com.aemreunal.config.GlobalSettings;
import com.aemreunal.controller.DeleteResponse;
import com.aemreunal.domain.User;
import com.aemreunal.exception.user.UsernameClashException;
import com.aemreunal.service.UserService;

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

@Controller
@RequestMapping(GlobalSettings.USER_PATH_MAPPING)
public class UserController {
    @Autowired
    private UserService userService;

//    /**
//     * Get the user with the specified ID
//     *
//     * @param userId
//     *     The ID of the user
//     *
//     * @return The user
//     */
//    @RequestMapping(method = RequestMethod.GET, value = "/{userId}", produces = "application/json;charset=UTF-8")
//    public ResponseEntity<User> getUserById(@PathVariable Long userId) {
//        User user = userService.findById(userId);
//        if (user == null) {
//            return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
//        }
//        return new ResponseEntity<User>(user, HttpStatus.OK);
//    }

    /**
     * Get the user with the specified username
     *
     * @param username
     *     The username of the user
     *
     * @return The user
     */
    // TODO require authentication to get details
    @RequestMapping(method = RequestMethod.GET, value = GlobalSettings.USER_USERNAME_MAPPING, produces = "application/json;charset=UTF-8")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        User user = userService.findByUsername(username);
        if (user == null) {
            return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<User>(user, HttpStatus.OK);
    }

    /**
     * Create a new user
     *
     * @param userJson
     *     The user as a JSON object
     * @param builder
     *     The URI builder for post-creation redirect
     *
     * @return The created project
     *
     * @throws UsernameClashException
     */
    @RequestMapping(method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public ResponseEntity<User> createUser(
        @RequestBody JSONObject userJson,
        UriComponentsBuilder builder) throws UsernameClashException {
        User savedUser = new User(userJson);
        verifyUsernameUniqueness(savedUser.getUsername());
        try {
            savedUser = userService.save(savedUser);
        } catch (ConstraintViolationException | TransactionSystemException e) {
            if (GlobalSettings.DEBUGGING) {
                System.err.println("Unable to save user! Constraint violation detected!");
            }
            return new ResponseEntity<User>(HttpStatus.BAD_REQUEST);
        }
        if (GlobalSettings.DEBUGGING) {
            System.out.println("Saved user with username = \'" + savedUser.getUsername() + "\' ID = \'" + savedUser.getUserId() + "\'");
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(builder.path(GlobalSettings.USER_SPECIFIC_MAPPING)
                                   .buildAndExpand(
                                       savedUser.getUsername())
                                   .toUri());
        return new ResponseEntity<User>(savedUser, headers, HttpStatus.CREATED);
    }

    /**
     * Checks whether the specified username exists and if so, throws a
     * UsernameClashException.
     *
     * @param username
     *     The username to check
     *
     * @throws UsernameClashException
     */
    private void verifyUsernameUniqueness(String username) throws UsernameClashException {
        if (userService.isUsernameTaken(username)) {
            throw new UsernameClashException(username);
        }
    }

    /**
     * Delete the specified user, along with all the projects belonging to user.
     * <p/>
     * To delete the user, confirmation must be supplied as a URI parameter, in the form
     * of "?confirm=yes". If not supplied, the user will not be deleted.
     *
     * @param username
     *     The username of the user to delete
     * @param confirmation
     *     The confirmation parameter
     *
     * @return The status of the deletion action
     */
    @RequestMapping(method = RequestMethod.DELETE, value = GlobalSettings.USER_USERNAME_MAPPING)
    public ResponseEntity<User> deleteUser(
        @PathVariable String username,
        @RequestParam(value = "confirm", required = true) String confirmation/*,
        TODO Instead of getting password via a parameter, just authenticate user with oauth:
        Define: "/user/XXX has access to everyting beyond XXX/... if it authenticates with the
        credentials of XXX" -> variable authentication.
        @RequestParam(value = "password", required = true) String password*/) {

        DeleteResponse response = DeleteResponse.NOT_DELETED;
        if (confirmation.toLowerCase().equals("yes")) {
//            if (userService.authenticateAndFindUser(username, password) == null) {
//            TODO throw new authentication exception
//                response = DeleteResponse.FORBIDDEN;
//            } else {
            response = userService.delete(username);
//            }
        }

        switch (response) {
            case DELETED:
                return new ResponseEntity<User>(HttpStatus.OK);
            case FORBIDDEN:
                return new ResponseEntity<User>(HttpStatus.FORBIDDEN);
            case NOT_FOUND:
                return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
            case NOT_DELETED:
                return new ResponseEntity<User>(HttpStatus.PRECONDITION_FAILED);
            default:
                return new ResponseEntity<User>(HttpStatus.I_AM_A_TEAPOT);
        }
    }
}
