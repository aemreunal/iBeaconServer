package com.aemreunal.domain;

import net.minidev.json.JSONObject;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.Size;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.web.bind.annotation.ResponseBody;
import com.aemreunal.config.CoreConfig;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/*
 **************************
 * Copyright (c) 2014     *
 *                        *
 * This code belongs to:  *
 *                        *
 * Ahmet Emre Ünal        *
 * S001974                *
 *                        *
 * aemreunal@gmail.com    *
 * emre.unal@ozu.edu.tr   *
 *                        *
 * aemreunal.com          *
 **************************
 */

@Entity
@Table(name = "users")
@ResponseBody
@JsonIgnoreProperties(value = { "password", "projects" })
public class User extends ResourceSupport implements Serializable {
    public static final int USERNAME_MIN_LENGTH = 4;
    public static final int USERNAME_MAX_LENGTH = 50;
    // The BCrypt-hashed password field length is assumed to be 60 with a
    // 2-digit log factor. For example, in '$2a$10$...', the '10' is the log
    // factor. If it ever gets a 3-digit log factor (highly unlikely), the
    // length of this field must become 61.
    public static final int BCRYPT_HASH_LENGTH = 60;

    public User() {
        // Empty constructor for Spring & Hibernate
    }

    public User(JSONObject userJson) {
        setUsername(userJson.get("username").toString());
        setPassword(userJson.get("password").toString());
    }

    /*
     *------------------------------------------------------------
     * BEGIN: User 'ID' attribute
     */
    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long userId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
    /*
     * END: User 'ID' attribute
     *------------------------------------------------------------
     */

    /*
     *------------------------------------------------------------
     * BEGIN: User 'username' attribute
     * This is the username of the user, used for authentication
     */
    @Column(name = "username", nullable = false, length = USERNAME_MAX_LENGTH, unique = true)
    // TODO check if username starts with a letter
    @Size(min = USERNAME_MIN_LENGTH, max = USERNAME_MAX_LENGTH)
    private String username = "";

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    /*
     * END: User 'username' attribute
     *------------------------------------------------------------
     */

    /*
     *------------------------------------------------------------
     * BEGIN: User 'password' attribute
     * This is the BCrypt hashed password of the user
     * org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
     */
    @Column(name = "password", nullable = false)
    @Size(min = BCRYPT_HASH_LENGTH, max = BCRYPT_HASH_LENGTH)
    private String password = "";

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    /*
     * END: User 'hashedPassword' attribute
     *------------------------------------------------------------
     */

    // TODO add users' real name, surname, email, secret questions

    /*
     *------------------------------------------------------------
     * BEGIN: User 'projects' attribute
     */
    @OneToMany(targetEntity = Project.class,
               mappedBy = "owner",
               orphanRemoval = true,
               fetch = FetchType.LAZY)
    @OrderBy("projectId")
    private Set<Project> projects = new LinkedHashSet<>();

    public Set<Project> getProjects() {
        CoreConfig.initLazily(projects);
        return projects;
    }

    public void setProjects(Set<Project> projects) {
        CoreConfig.initLazily(projects);
        this.projects = projects;
    }
    /*
     * END: User 'projects' attribute
     *------------------------------------------------------------
     */

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof User)) {
            return false;
        } else {
            User otherUser = (User) obj;
            boolean idsAreEqual = this.getUserId() == otherUser.getUserId();
            boolean usernamesAreEqual = this.getUsername().equals(otherUser.getUsername());
            return idsAreEqual && usernamesAreEqual;
        }
    }
}
