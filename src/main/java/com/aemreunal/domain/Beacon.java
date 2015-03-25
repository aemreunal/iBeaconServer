package com.aemreunal.domain;


/*
 * *************************
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
 * *************************
 */

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.Size;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.web.bind.annotation.ResponseBody;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

// To not mark getters & setters as unused, as they're being used by Spring & Hibernate
@SuppressWarnings("UnusedDeclaration")

@Entity
@Table(name = "beacons")
@ResponseBody
@JsonIgnoreProperties(value = { "project", "region" })
public class Beacon extends ResourceSupport implements Serializable {
    // UUID hex string (including dashes) is 36 characters long
    public static final int UUID_MAX_LENGTH        = 36;
    public static final int DESCRIPTION_MAX_LENGTH = 200;

    /*
     *------------------------------------------------------------
     * BEGIN: Beacon 'ID' attribute
     */
    @Id
    @Column(name = "beacon_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @OrderColumn
    @Access(AccessType.PROPERTY)
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
    @Access(AccessType.PROPERTY)
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
    @Column(name = "major", nullable = false)
    @Access(AccessType.PROPERTY)
    private Integer major = -1;

    public Integer getMajor() {
        return major;
    }

    public void setMajor(Integer major) {
        this.major = major;
    }
    /*
     * END: Beacon 'Major' attribute
     *------------------------------------------------------------
     */

    /*
     *------------------------------------------------------------
     * BEGIN: Beacon 'Minor' attribute
     */
    @Column(name = "minor", nullable = false)
    @Access(AccessType.PROPERTY)
    private Integer minor = -1;

    public Integer getMinor() {
        return minor;
    }

    public void setMinor(Integer minor) {
        this.minor = minor;
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
    @Access(AccessType.PROPERTY)
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
     * BEGIN: Beacon 'region' attribute
     */
    @ManyToOne(targetEntity = Region.class,
            fetch = FetchType.LAZY,
            optional = true)
    // JoinTable & Lazy fetch-> 5.1.7: http://docs.jboss.org/hibernate/core/4.3/manual/en-US/html_single/
    @JoinTable(name = "regions_to_beacons",
            joinColumns = @JoinColumn(name = "beacon_id"),
            inverseJoinColumns = @JoinColumn(name = "region_id"))
    @Access(AccessType.PROPERTY)
    private Region region;

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }
    /*
     * END: Beacon 'region' attribute
     *------------------------------------------------------------
     */


    /*
     *------------------------------------------------------------
     * BEGIN: Beacon 'X coordinate' attribute
     */
    @Column(name = "x_coordinate", nullable = false)
    @Access(AccessType.PROPERTY)
    private Integer xCoordinate;

    public Integer getxCoordinate() {
        return xCoordinate;
    }

    public void setxCoordinate(Integer xCoordinate) {
        this.xCoordinate = xCoordinate;
    }
    /*
     * END: Beacon 'X coordinate' attribute
     *------------------------------------------------------------
     */


    /*
     *------------------------------------------------------------
     * BEGIN: Beacon 'Y coordinate' attribute
     */
    @Column(name = "y_coordinate", nullable = false)
    @Access(AccessType.PROPERTY)
    private Integer yCoordinate;

    public Integer getyCoordinate() {
        return yCoordinate;
    }

    public void setyCoordinate(Integer yCoordinate) {
        this.yCoordinate = yCoordinate;
    }
    /*
     * END: Beacon 'Y coordinate' attribute
     *------------------------------------------------------------
     */


    /*
     *------------------------------------------------------------
     * BEGIN: Beacon 'scenario' attribute
     */
    @ManyToOne(targetEntity = Scenario.class,
            fetch = FetchType.LAZY,
            optional = false)
    @JoinTable(name = "scenarios_to_beacons",
            joinColumns = @JoinColumn(name = "beacon_id"),
            inverseJoinColumns = @JoinColumn(name = "scenario_id"))
    @Access(AccessType.PROPERTY)
    private Scenario scenario;

    public Scenario getScenario() {
        return scenario;
    }

    public void setScenario(Scenario scenario) {
        this.scenario = scenario;
    }
    /*
     * END: Beacon 'scenario' attribute
     *------------------------------------------------------------
     */


    /*
     *------------------------------------------------------------
     * BEGIN: Beacon 'designated' attribute
     */
    @Column(name = "designated", nullable = false)
    @Access(AccessType.PROPERTY)
    private Boolean designated = false;

    public Boolean getDesignated() {
        return designated;
    }

    public void setDesignated(Boolean designated) {
        this.designated = designated;
    }
    /*
     * END: Beacon 'designated' attribute
     *------------------------------------------------------------
     */


    /*
     *------------------------------------------------------------
     * BEGIN: Beacon 'creationDate' attribute
     */
    @Column(name = "creation_date", nullable = false)
    @Access(AccessType.PROPERTY)
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
        if (obj instanceof Beacon) {
            return ((Beacon) obj).getBeaconId() == this.getBeaconId();
        }
        return false;
    }
}
