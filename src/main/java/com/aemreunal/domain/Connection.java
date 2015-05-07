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
import net.minidev.json.JSONObject;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.*;
import javax.validation.constraints.Size;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.web.bind.annotation.ResponseBody;
import com.aemreunal.helper.json.JsonBuilderFactory;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "connections")
@ResponseBody
@JsonIgnoreProperties(value = { "project", "beacons", "connectionImageFileName" })
public class Connection extends ResourceSupport implements Serializable, Comparable {
    // UUID hex string (including dashes) is 36 characters long
    public static final int UUID_MAX_LENGTH = 36;

    /*
     *------------------------------------------------------------
     * BEGIN: Connection 'ID' attribute
     */
    @Id
    @Column(name = "connection_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Access(AccessType.PROPERTY)
    private Long connectionId;
    /*
     * END: Connection 'ID' attribute
     *------------------------------------------------------------
     */

    /*
     *------------------------------------------------------------
     * BEGIN: Connection 'Image file name' attribute
     *
     * The length is UUID_MAX_LENGTH, because image names are set as UUIDs.
     */
    @Column(name = "connection_image_name", nullable = false, length = UUID_MAX_LENGTH)
    @Size(min = UUID_MAX_LENGTH, max = UUID_MAX_LENGTH)
    @Access(AccessType.PROPERTY)
    private String connectionImageFileName = "";
    /*
     * END: Connection 'Image file name' attribute
     *------------------------------------------------------------
     */

    /*
     *------------------------------------------------------------
     * BEGIN: Connection 'beacons' attribute
     */
    @ManyToMany(targetEntity = Beacon.class,
            mappedBy = "connections",
            fetch = FetchType.LAZY)
    @Access(AccessType.PROPERTY)
    @OrderBy("beaconId")
    private Set<Beacon> beacons = new LinkedHashSet<Beacon>();
    /*
     * END: Connection 'beacons' attribute
     *------------------------------------------------------------
     */

    /*
     *------------------------------------------------------------
     * BEGIN: Connection 'project' attribute
     */
    @ManyToOne(targetEntity = Project.class,
            optional = false,
            fetch = FetchType.LAZY)
    @JoinTable(name = "projects_to_connections",
            joinColumns = @JoinColumn(name = "connection_id"),
            inverseJoinColumns = @JoinColumn(name = "project_id"))
    @Access(AccessType.PROPERTY)
    private Project project;
    /*
     * END: Connection 'project' attribute
     *------------------------------------------------------------
     */

    /*
     *------------------------------------------------------------
     * BEGIN: Helpers
     */
    public void addBeacon(Beacon beacon) {
        this.getBeacons().add(beacon);
    }

    public JSONArray getBeaconIdsAsJson() {
        return JsonBuilderFactory.array()
                                 .addAll(getBeacons().stream()
                                                     .map(Beacon::getBeaconId)
                                                     .collect(Collectors.toList()))
                                 .build();
    }

    public JSONObject getQueryResponse() {
        return JsonBuilderFactory.object()
                                 .add("connectionId", getConnectionId())
                                 .add("beacons", getBeaconIdsAsJson())
                                 .build();
    }
    /*
     * END: Helpers
     *------------------------------------------------------------
     */

    /*
     *------------------------------------------------------------
     * BEGIN: Constructors
     */
    public Connection() {
        // Empty constructor for Spring & Hibernate
    }
    /*
     * END: Constructors
     *------------------------------------------------------------
     */

    /*
     *------------------------------------------------------------
     * BEGIN: Getters & Setters
     */
    public Long getConnectionId() {
        return connectionId;
    }

    public void setConnectionId(Long connectionId) {
        this.connectionId = connectionId;
    }

    public String getConnectionImageFileName() {
        return connectionImageFileName;
    }

    public void setConnectionImageFileName(String connectionImageFileName) {
        this.connectionImageFileName = connectionImageFileName;
    }

    public Set<Beacon> getBeacons() {
        return beacons;
    }

    public void setBeacons(Set<Beacon> beacons) {
        this.beacons = beacons;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }
    /*
     * END: Getters & Setters
     *------------------------------------------------------------
     */

    @Override
    public String toString() {
        return "[Connection: " + getConnectionId() +
                ", Project: " + getProject().getProjectId() + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Connection) {
            return ((Connection) obj).getConnectionId().equals(this.getConnectionId());
        }
        return false;
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof Connection) {
            return this.getConnectionId().compareTo(((Connection) o).getConnectionId());
        }
        return 0;
    }
}
