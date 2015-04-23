package com.aemreunal.domain;

/*
 * *********************** *
 * Copyright (c) 2015      *
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
 * *********************** *
 */

import net.minidev.json.JSONObject;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.Size;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.web.bind.annotation.ResponseBody;
import com.aemreunal.helper.json.JsonBuilderFactory;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "projects")
@ResponseBody
@JsonIgnoreProperties(value = { "beacons", "regions", "scenarios", "projectSecret", "owner", "connections" })
public class Project extends ResourceSupport implements Serializable, Comparable {
    public static final int NAME_MAX_LENGTH        = 50;
    public static final int DESCRIPTION_MAX_LENGTH = 200;
    // The BCrypt-hashed secret field length is assumed to be 60 with a
    // 2-digit log factor. For example, in '$2a$10$...', the '10' is the log
    // factor. If it ever gets a 3-digit log factor (highly unlikely), the
    // length of this field must become 61.
    public static final int BCRYPT_HASH_LENGTH     = 60;

    /*
     *------------------------------------------------------------
     * BEGIN: Project 'ID' attribute
     */
    @Id
    @Column(name = "project_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Access(AccessType.PROPERTY)
    private Long projectId;
    /*
     * END: Project 'ID' attribute
     *------------------------------------------------------------
     */

    /*
     *------------------------------------------------------------
     * BEGIN: Project 'name' attribute
     */
    @Column(name = "name", nullable = false, length = NAME_MAX_LENGTH)
    @Size(min = 1, max = NAME_MAX_LENGTH)
    @Access(AccessType.PROPERTY)
    private String name = "";
    /*
     * END: Project 'name' attribute
     *------------------------------------------------------------
     */

    /*
     *------------------------------------------------------------
     * BEGIN: Project 'description' attribute
     */
    @Column(name = "description", nullable = false, length = DESCRIPTION_MAX_LENGTH)
    @Size(max = DESCRIPTION_MAX_LENGTH)
    @Access(AccessType.PROPERTY)
    private String description = "";
    /*
     * END: Project 'description' attribute
     *------------------------------------------------------------
     */

    /*
     *------------------------------------------------------------
     * BEGIN: Project 'creationDate' attribute
     */
    @Column(name = "creation_date", nullable = false)
    @Access(AccessType.PROPERTY)
    private Date creationDate = null;
    /*
     * END: Project 'creationDate' attribute
     *------------------------------------------------------------
     */

    /*
     *------------------------------------------------------------
     * BEGIN: Project 'owner' attribute
     */
    @ManyToOne(targetEntity = User.class,
            fetch = FetchType.LAZY,
            optional = false)
    @JoinTable(name = "users_to_projects",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    @Access(AccessType.PROPERTY)
    private User owner;

    // TODO other users
    /*
     * END: Project 'owner' attribute
     *------------------------------------------------------------
     */

    /*
     *------------------------------------------------------------
     * BEGIN: Project 'region list' attribute
     */
    @OneToMany(targetEntity = Region.class,
            mappedBy = "project",
            orphanRemoval = true,
            fetch = FetchType.LAZY,
            cascade = CascadeType.REMOVE)
    @OrderBy(value = "regionId")
    @Access(AccessType.PROPERTY)
    private Set<Region> regions = new LinkedHashSet<>();
    /*
     * END: Project 'region list' attribute
     *------------------------------------------------------------
     */

    /*
     *------------------------------------------------------------
     * BEGIN: Project 'connection list' attribute
     */
    @OneToMany(targetEntity = Connection.class,
            mappedBy = "project",
            orphanRemoval = true,
            fetch = FetchType.LAZY,
            cascade = CascadeType.REMOVE)
    @OrderBy(value = "connectionId")
    @Access(AccessType.PROPERTY)
    private Set<Connection> connections = new LinkedHashSet<>();
    /*
     * END: Project 'connection list' attribute
     *------------------------------------------------------------
     */

    /*
     *------------------------------------------------------------
     * BEGIN: Project 'scenarios list' attribute
     */
    @OneToMany(targetEntity = Scenario.class,
            mappedBy = "project",
            orphanRemoval = true,
            fetch = FetchType.LAZY,
            cascade = CascadeType.REMOVE)
    @OrderBy(value = "scenarioId")
    @Access(AccessType.PROPERTY)
    private Set<Scenario> scenarios = new LinkedHashSet<>();
    /*
     * END: Project 'scenarios list' attribute
     *------------------------------------------------------------
     */

    /*
     *------------------------------------------------------------
     * BEGIN: Project 'secret' attribute
     *
     * TODO add resetting secret
     */
    @Column(name = "project_secret", nullable = false, unique = false)
    @Size(min = BCRYPT_HASH_LENGTH, max = BCRYPT_HASH_LENGTH)
    @Access(AccessType.PROPERTY)
    private String projectSecret = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX";
    /*
     * END: Project 'secret' attribute
     *------------------------------------------------------------
     */

    /*
     *------------------------------------------------------------
     * BEGIN: Constructors
     */
    public Project() {
        // Empty constructor for Spring & Hibernate
    }
    /*
     * END: Constructors
     *------------------------------------------------------------
     */

    /*
     *------------------------------------------------------------
     * BEGIN: Helpers
     */
    public JSONObject getCreateResponse(String projectSecret) {
        return JsonBuilderFactory.object()
                                 .add("projectId", getProjectId())
                                 .add("name", getName())
                                 .add("description", getDescription())
                                 .add("secret", projectSecret)
                                 .add("links", getLinks())
                                 .build();
    }

    public JSONObject getQueryResponse() {
        return JsonBuilderFactory.object()
                                 .add("projectId", getProjectId())
                                 .add("links", getLinks())
                                 .build();
    }
    /*
     * END: Helpers
     *------------------------------------------------------------
     */

    /*
     *------------------------------------------------------------
     * BEGIN: Getters & Setters
     */
    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public Set<Region> getRegions() {
        return regions;
    }

    public void setRegions(Set<Region> regions) {
        this.regions = regions;
    }

    public Set<Connection> getConnections() {
        return connections;
    }

    public void setConnections(Set<Connection> connections) {
        this.connections = connections;
    }

    public Set<Scenario> getScenarios() {
        return scenarios;
    }

    public void setScenarios(Set<Scenario> scenarios) {
        this.scenarios = scenarios;
    }

    public String getProjectSecret() {
        return projectSecret;
    }

    public void setProjectSecret(String projectSecret) {
        this.projectSecret = projectSecret;
    }
    /*
     * END: Getters & Setters
     *------------------------------------------------------------
     */

    @PrePersist
    private void setInitialProperties() {
        // Set project creation date
        if (creationDate == null) {
            setCreationDate(new Date());
        }
    }

    @Override
    public String toString() {
        return "[Project: " + getProjectId() +
                ", User: " + getOwner().getUsername() + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Project) {
            return ((Project) obj).getProjectId().equals(this.getProjectId());
        }
        return false;
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof Project) {
            return this.getProjectId().compareTo(((Project) o).getProjectId());
        }
        return 0;
    }
}

