package com.aemreunal.domain;

import net.minidev.json.JSONObject;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.Size;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.web.bind.annotation.ResponseBody;
import com.aemreunal.config.CoreConfig;
import com.aemreunal.helper.JsonBuilder;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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
 * List bug:
 * https://hibernate.atlassian.net/browse/HHH-6776
 * https://hibernate.atlassian.net/browse/HHH-5855
 * http://vladmihalcea.com/2013/10/16/hibernate-facts-favoring-sets-vs-bags/
 * https://access.redhat.com/documentation/en-US/JBoss_Enterprise_Application_Platform/4.3/html/Hibernate_Reference_Guide/Persistent_Classes-Implementing_equals_and_hashCode.html
 * http://stackoverflow.com/questions/7903800/hibernate-inserts-duplicates-into-a-onetomany-collection
 */

@Entity
@Table(name = "projects")
@ResponseBody
@JsonIgnoreProperties(value = { "beacons", "beaconGroups", "scenarios", "projectSecret", "owner", "creationDate" })
public class Project extends ResourceSupport implements Serializable {
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
    @OrderColumn
    private Long projectId;

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }
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
    private String name = "";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
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
    private String description = "";

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    /*
     * END: Project 'description' attribute
     *------------------------------------------------------------
     */

    /*
     *------------------------------------------------------------
     * BEGIN: Project 'creationDate' attribute
     */
    @Column(name = "creation_date", nullable = false)
    private Date creationDate = null;

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

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
    private User owner;

    public User getOwner() {
        CoreConfig.initLazily(owner);
        return owner;
    }

    public void setOwner(User owner) {
        CoreConfig.initLazily(owner);
        this.owner = owner;
    }

    // TODO other users
    /*
     * END: Project 'owner' attribute
     *------------------------------------------------------------
     */

    /*
     *------------------------------------------------------------
     * BEGIN: Project 'beacons list' attribute
     */
    @OneToMany(targetEntity = Beacon.class,
               mappedBy = "project",
               orphanRemoval = true,
               fetch = FetchType.LAZY)
    /*
     * To eliminate duplicates without using Sets, use:
     * @Fetch(FetchMode.SUBSELECT)
     * via: http://doctorjw.wordpress.com/2012/01/11/hibernate-collections-and-duplicate-objects/
     */
    @OrderBy("beaconId")
    private Set<Beacon> beacons = new LinkedHashSet<>();

    public Set<Beacon> getBeacons() {
        CoreConfig.initLazily(beacons);
        return beacons;
    }

    public void setBeacons(Set<Beacon> beacons) {
        this.beacons = beacons;
    }

    public void addBeacon(Beacon beacon) {
        CoreConfig.initLazily(beacons);
        this.beacons.add(beacon);
    }
    /*
     * END: Project 'beacons list' attribute
     *------------------------------------------------------------
     */

    /*
     *------------------------------------------------------------
     * BEGIN: Project 'beacon groups list' attribute
     */
    @OneToMany(targetEntity = BeaconGroup.class,
               mappedBy = "project",
               orphanRemoval = true,
               fetch = FetchType.LAZY)
    @OrderBy(value = "beaconGroupId")
    private Set<BeaconGroup> beaconGroups = new LinkedHashSet<>();

    public Set<BeaconGroup> getBeaconGroups() {
        CoreConfig.initLazily(beaconGroups);
        return beaconGroups;
    }

    public void setBeaconGroups(Set<BeaconGroup> beaconGroups) {
        this.beaconGroups = beaconGroups;
    }

    public void addBeaconGroup(BeaconGroup beaconGroup) {
        CoreConfig.initLazily(beaconGroups);
        this.beaconGroups.add(beaconGroup);
    }
    /*
     * END: Project 'beacon groups list' attribute
     *------------------------------------------------------------
     */

    /*
     *------------------------------------------------------------
     * BEGIN: Project 'scenarios list' attribute
     */
    @OneToMany(targetEntity = Scenario.class,
               mappedBy = "project",
               orphanRemoval = true,
               fetch = FetchType.LAZY)
    @OrderBy(value = "scenarioId")
    private Set<Scenario> scenarios = new LinkedHashSet<>();

    public Set<Scenario> getScenarios() {
        CoreConfig.initLazily(scenarios);
        return scenarios;
    }

    public void setScenarios(Set<Scenario> scenarios) {
        CoreConfig.initLazily(scenarios);
        this.scenarios = scenarios;
    }
    /*
     * END: Project 'scenarios list' attribute
     *------------------------------------------------------------
     */

    /*
     *------------------------------------------------------------
     * BEGIN: Project 'secret' attribute
     */
    @Column(name = "project_secret", nullable = false, unique = false)
    @Size(min = BCRYPT_HASH_LENGTH, max = BCRYPT_HASH_LENGTH)
    /*
     * TODO add resetting secret
     */
    private String projectSecret = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX";

    public String getProjectSecret() {
        return projectSecret;
    }

    public void setProjectSecret(String projectSecret) {
        this.projectSecret = projectSecret;
    }
    /*
     * END: Project 'secret' attribute
     *------------------------------------------------------------
     */

    @PrePersist
    private void setInitialProperties() {
        /*
        // Generate project secret key
        if (projectSecret.equals("")) {
            this.setProjectSecret(UUID.randomUUID().toString());
        }
        */
        // Set project creation date
        if (creationDate == null) {
            setCreationDate(new Date());
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Project)) {
            return false;
        } else {
            return ((Project) obj).getProjectId().equals(this.getProjectId());
        }
    }

    public JSONObject getCreateResponse(String projectSecret) {
        return new JsonBuilder().add("projectId", getProjectId())
                                .add("name", getName())
                                .add("description", getDescription())
                                .add("secret", projectSecret)
                                .add("links", getLinks())
                                .build();
    }
}

