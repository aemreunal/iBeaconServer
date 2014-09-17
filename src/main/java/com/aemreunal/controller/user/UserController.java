package com.aemreunal.controller.user;

import net.minidev.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import com.aemreunal.config.GlobalSettings;
import com.aemreunal.domain.User;
import com.aemreunal.exception.MalformedRequestException;
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

    /**
     * Get the user with the specified username
     *
     * @param username
     *     The username of the user
     *
     * @return The user
     */
    // TODO require authentication to get details
    @RequestMapping(method = RequestMethod.GET,
                    value = GlobalSettings.USER_USERNAME_MAPPING,
                    produces = "application/json; charset=UTF-8")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        User user = userService.findByUsername(username);
        return new ResponseEntity<User>(user, HttpStatus.OK);
    }

    /**
     * Create a new user
     * <p/>
     * User creation request JSON:<br/> {<br/> "username":"testuser12",<br/>
     * "password":"test_password" }
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
    @RequestMapping(method = RequestMethod.POST, value = GlobalSettings.USER_CREATE_MAPPING, produces = "application/json; charset=UTF-8")
    public ResponseEntity<User> createUser(@RequestBody JSONObject userJson,
                                           UriComponentsBuilder builder)
        throws UsernameClashException, MalformedRequestException {
        verifyUserCreateJson(userJson);
        User savedUser = userService.save(new User(userJson));
        if (GlobalSettings.DEBUGGING) {
            System.out.println("Saved user with username = \'" + savedUser.getUsername() + "\' ID = \'" + savedUser.getUserId() + "\'");
        }
        return buildCreateResponse(builder, savedUser);
    }

    // TODO verify JSON inputs
    private void verifyUserCreateJson(JSONObject userJson) throws MalformedRequestException {
        if (!userJson.containsKey("username") || !userJson.containsKey("password")) {
            throw new MalformedRequestException();
        }
    }

    private ResponseEntity<User> buildCreateResponse(UriComponentsBuilder builder, User savedUser) {
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(builder.path(GlobalSettings.USER_SPECIFIC_MAPPING)
                                   .buildAndExpand(
                                       savedUser.getUsername())
                                   .toUri());
        return new ResponseEntity<User>(savedUser, headers, HttpStatus.CREATED);
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
    /*
        TODO Instead of getting password via a parameter, just authenticate user with oauth:
        Define: "/user/XXX has access to everyting beyond XXX/... if it authenticates with the
        credentials of XXX" -> variable authentication.
        @RequestParam(value = "password", required = true) String password
     */
    @RequestMapping(method = RequestMethod.DELETE, value = GlobalSettings.USER_USERNAME_MAPPING)
    public ResponseEntity<User> deleteUser(
        @PathVariable String username,
        @RequestParam(value = "confirm", required = true) String confirmation) {
        if (confirmation.toLowerCase().equals("yes")) {
            User user = userService.delete(username);
            return new ResponseEntity<User>(user, HttpStatus.OK);
        } else {
            return new ResponseEntity<User>(HttpStatus.PRECONDITION_FAILED);
        }
    }
}
