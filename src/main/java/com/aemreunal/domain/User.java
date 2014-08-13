package com.aemreunal.domain;

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
@JsonIgnoreProperties(value = { "projects" })
public class User extends ResourceSupport implements Serializable {
    public static final int USERNAME_MIN_LENGTH = 4;
    public static final int USERNAME_MAX_LENGTH = 50;

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
    // TODO check if user with that username exists
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
}
