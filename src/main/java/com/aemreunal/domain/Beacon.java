package com.aemreunal.domain;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.Size;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.web.bind.annotation.ResponseBody;
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
@Table(name = "beacons")
@ResponseBody
@JsonIgnoreProperties(value = { "links" })
public class Beacon extends ResourceSupport implements Serializable {
    // UUID hex string (including dashes) is 36 characters long
    public static final int UUID_MAX_LENGTH = 36;
    // Major hex string is 4 characters long
    public static final int MAJOR_MIN_LENGTH = 1;
    public static final int MAJOR_MAX_LENGTH = 4;
    // Minor hex string is 4 characters long
    public static final int MINOR_MIN_LENGTH = 1;
    public static final int MINOR_MAX_LENGTH = 4;
    public static final int DESCRIPTION_MAX_LENGTH = 200;

    /*
     *------------------------------------------------------------
     * BEGIN: Beacon 'ID' attribute
     */
    @Id
    @Column(name = "beacon_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long beaconId;

    public Long getBeaconId() {
        return beaconId;
    }

    public void setBeaconId(Long beaconId) {
        this.beaconId = beaconId;
    }
    /*
     * END: Beacon 'ID' attribute
     *------------------------------------------------------------
     */

    /*
     *------------------------------------------------------------
     * BEGIN: Beacon 'UUID' attribute
     */
    @Column(name = "uuid", nullable = false, length = UUID_MAX_LENGTH)
    @Size(min = UUID_MAX_LENGTH, max = UUID_MAX_LENGTH)
    private String uuid = "";

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid.toUpperCase();
    }
    /*
     * END: Beacon 'UUID' attribute
     *------------------------------------------------------------
     */

    /*
     *------------------------------------------------------------
     * BEGIN: Beacon 'Major' attribute
     */
    @Column(name = "major", nullable = false, length = MAJOR_MAX_LENGTH)
    @Size(min = MAJOR_MIN_LENGTH, max = MAJOR_MAX_LENGTH)
    private String major = "";

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major.toUpperCase();
    }
    /*
     * END: Beacon 'Major' attribute
     *------------------------------------------------------------
     */

    /*
     *------------------------------------------------------------
     * BEGIN: Beacon 'Minor' attribute
     */
    @Column(name = "minor", nullable = false, length = MINOR_MAX_LENGTH)
    @Size(min = MINOR_MIN_LENGTH, max = MINOR_MAX_LENGTH)
    private String minor = "";

    public String getMinor() {
        return minor;
    }

    public void setMinor(String minor) {
        this.minor = minor.toUpperCase();
    }
    /*
     * END: Beacon 'Minor' attribute
     *------------------------------------------------------------
     */

    /*
     *------------------------------------------------------------
     * BEGIN: Beacon 'description' attribute
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
     * END: Beacon 'description' attribute
     *------------------------------------------------------------
     */

    /*
     *------------------------------------------------------------
     * BEGIN: Beacon 'group' attribute
     */
    @ManyToOne(targetEntity = BeaconGroup.class,
        fetch = FetchType.EAGER,
        optional = true)
    @JsonIgnoreProperties(value = { "name", "description", "beacons", "project", "creationDate" })
    private BeaconGroup group;

    public BeaconGroup getGroup() {
        return group;
    }

    public void setGroup(BeaconGroup group) {
        this.group = group;
    }
    /*
     * END: Beacon 'group' attribute
     *------------------------------------------------------------
     */

    /*
     *------------------------------------------------------------
     * BEGIN: Beacon 'project' attribute
     */
    @ManyToOne(targetEntity = Project.class,
        fetch = FetchType.EAGER,
        optional = false)
    @JsonIgnoreProperties(value = { "name", "description", "beacons", "beaconGroups", "scenarios", "projectSecret", "creationDate" })
    private Project project;

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }
    /*
     * END: Beacon 'project' attribute
     *------------------------------------------------------------
     */


    /*
     *------------------------------------------------------------
     * BEGIN: Beacon 'creationDate' attribute
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
     * END: Beacon 'creationDate' attribute
     *------------------------------------------------------------
     */

    @PrePersist
    private void setInitialProperties() {
        // Set beacon creation date
        if (creationDate == null) {
            setCreationDate(new Date());
        }
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Beacon)) {
            return false;
        } else {
            return ((Beacon) obj).getBeaconId() == this.getBeaconId();
        }
    }
}
