package com.aemreunal.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.persistence.*;
import javax.validation.constraints.Size;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.web.bind.annotation.ResponseBody;
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

@Entity
@Table(name = "projects")
@ResponseBody
@JsonIgnoreProperties(value = { "links" })
public class Project extends ResourceSupport implements Serializable {
    public static final int NAME_MAX_LENGTH = 50;
    public static final int DESCRIPTION_MAX_LENGTH = 200;
    public static final int SECRET_LENGTH = 36;

    /*
     *------------------------------------------------------------
     * BEGIN: Project 'ID' attribute
     */
    @Id
    @Column(name = "project_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
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
//    @ManyToOne(targetEntity = User.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL, optional = true)
//    // MUST BE IMPLEMENTED
//    // TODO @JsonIgnoreProperties(value = { "name", "description", "beacons" })
//    @JsonIgnore
//    // MUST BE IMPLEMENTED
//    private User owner;
//
//    public User getOwner() {
//        return owner;
//    }
//
//    public void setOwner(User owner) {
//        this.owner = owner;
//    }

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
        fetch = FetchType.EAGER)
    @JsonIgnoreProperties(value = { "uuid", "major", "minor", "description", "group", "project", "creationDate" })
    private List<Beacon> beacons;

    public List<Beacon> getBeacons() {
        return beacons;
    }

    public void setBeacons(List<Beacon> beacons) {
        this.beacons = beacons;
    }

    public void addBeacon(Beacon beacon) {
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
        fetch = FetchType.EAGER)
    @JsonIgnoreProperties(value = { "name", "description", "beacons", "project", "creationDate" })
    private List<BeaconGroup> beaconGroups;

    public List<BeaconGroup> getBeaconGroups() {
        return beaconGroups;
    }

    public void setBeaconGroups(List<BeaconGroup> beaconGroups) {
        this.beaconGroups = beaconGroups;
    }

    public void addBeaconGroup(BeaconGroup beaconGroup) {
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
/*
    // MUST BE IMPLEMENTED
    // TODO @JsonIgnoreProperties(value = { "name", "description", "beacons" })
    @JsonIgnore
    // MUST BE IMPLEMENTED
    private List<Scenario> scenarios;

    public List<Scenario> getScenarios() {
        return scenarios;
    }

    public void setScenarios(List<Scenario> scenarios) {
        this.scenarios = scenarios;
    }
*/
    /*
     * END: Project 'scenarios list' attribute
     *------------------------------------------------------------
     */

    /*
     *------------------------------------------------------------
     * BEGIN: Project 'secret' attribute
     */
    @Column(name = "project_secret", nullable = false, unique = false)
    @Size(min = SECRET_LENGTH, max = SECRET_LENGTH)
    private String projectSecret = "";

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
        // Generate project secret key
        if (projectSecret.equals("")) {
            this.setProjectSecret(UUID.randomUUID().toString());
        }
        // Set project creation date
        if (creationDate == null) {
            setCreationDate(new Date());
        }
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Project)) {
            return false;
        } else {
            return ((Project) obj).getProjectId().equals(this.getProjectId());
        }
    }

}

