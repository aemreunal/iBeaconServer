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

import net.minidev.json.JSONArray;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.Size;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.web.bind.annotation.ResponseBody;
import com.aemreunal.helper.json.JsonArrayBuilder;
import com.aemreunal.helper.json.JsonBuilderFactory;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

// To not mark getters & setters as unused, as they're being used by Spring & Hibernate
@SuppressWarnings("UnusedDeclaration")

@Entity
@Table(name = "beacons")
@ResponseBody
@JsonIgnoreProperties(value = { "project", "region", "connections" })
public class Beacon extends ResourceSupport implements Serializable, Comparable {
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
    /*
     * END: Beacon 'designated' attribute
     *------------------------------------------------------------
     */

    /*
     *------------------------------------------------------------
     * BEGIN: Beacon 'connections' attribute
     */
    @ManyToMany(targetEntity = Connection.class,
            fetch = FetchType.LAZY)
    @JoinTable(name = "beacons_to_connections",
            joinColumns = @JoinColumn(name = "beacon_id"),
            inverseJoinColumns = @JoinColumn(name = "connection_id"))
    @OrderBy("connectionId")
    @Access(AccessType.PROPERTY)
    private Set<Connection> connections = new LinkedHashSet<Connection>();
    /*
     * END: Beacon 'connections' attribute
     *------------------------------------------------------------
     */

    /*
     *------------------------------------------------------------
     * BEGIN: Beacon 'creationDate' attribute
     */
    @Column(name = "creation_date", nullable = false)
    @Access(AccessType.PROPERTY)
    private Date creationDate = null;
    /*
     * END: Beacon 'creationDate' attribute
     *------------------------------------------------------------
     */

    /*
     *------------------------------------------------------------
     * BEGIN: Helpers
     */
    public void addConnection(Connection connection) {
        this.getConnections().add(connection);
    }

    public void removeConnection(Connection connection) {
        this.getConnections().remove(connection);
    }

    // Weird stuff: when this method is named something like 'getConnectionsJson',
    // 'getConnectionsList', 'getConnectionsAsList', Jackson tries to use it and
    // causes a LazyInit exception.
    @JsonIgnore
    public JSONArray getConnsAsJson() {
        JsonArrayBuilder connArray = JsonBuilderFactory.array();
        for (Connection connection : getConnections()) {
            connArray.add(connection.getBeaconIdsAsJson());
        }
        return connArray.build();
    }
    /*
     * END: Helpers
     *------------------------------------------------------------
     */

    /*
     *------------------------------------------------------------
     * BEGIN: Getters & Setters
     */
    public Long getBeaconId() {
        return beaconId;
    }

    public void setBeaconId(Long beaconId) {
        this.beaconId = beaconId;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid.toUpperCase();
    }

    public Integer getMajor() {
        return major;
    }

    public void setMajor(Integer major) {
        this.major = major;
    }

    public Integer getMinor() {
        return minor;
    }

    public void setMinor(Integer minor) {
        this.minor = minor;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public Integer getxCoordinate() {
        return xCoordinate;
    }

    public void setxCoordinate(Integer xCoordinate) {
        this.xCoordinate = xCoordinate;
    }

    public Integer getyCoordinate() {
        return yCoordinate;
    }

    public void setyCoordinate(Integer yCoordinate) {
        this.yCoordinate = yCoordinate;
    }

    public Scenario getScenario() {
        return scenario;
    }

    public void setScenario(Scenario scenario) {
        this.scenario = scenario;
    }

    public Boolean getDesignated() {
        return designated;
    }

    public Boolean isDesignated() {
        return designated;
    }

    public void setDesignated(Boolean designated) {
        this.designated = designated;
    }

    public Set<Connection> getConnections() {
        return connections;
    }

    public void setConnections(Set<Connection> connections) {
        this.connections = connections;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
    /*
     * END: Getters & Setters
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
            return ((Beacon) obj).getBeaconId().equals(this.getBeaconId());
        }
        return false;
    }

    @Override
    public String toString() {
        return "[Beacon: " + getBeaconId() + ", Region: "
                + getRegion().getRegionId() + ", Project: "
                + getRegion().getProject().getProjectId() + "]";
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof Beacon) {
            return this.getBeaconId().compareTo(((Beacon) o).getBeaconId());
        }
        return 0;
    }
}
