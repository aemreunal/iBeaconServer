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
import java.util.LinkedHashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.Size;
import org.springframework.hateoas.ResourceSupport;
import com.aemreunal.helper.JsonBuilder;

// To not mark getters & setters as unused, as they're being used by Spring & Hibernate
@SuppressWarnings("UnusedDeclaration")

@Entity
@Table(name = "connections")
public class Connection extends ResourceSupport implements Serializable {
    // UUID hex string (including dashes) is 36 characters long
    public static final int UUID_MAX_LENGTH = 36;

    /*
     *------------------------------------------------------------
     * BEGIN: Connection 'ID' attribute
     */
    @Id
    @Column(name = "connection_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @OrderColumn
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
        JsonBuilder beaconArray = new JsonBuilder(JsonBuilder.ARRAY);
        for (Beacon beacon : getBeacons()) {
            beaconArray = beaconArray.addToArr(beacon.getBeaconId());
        }
        return beaconArray.buildArr();
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
    public boolean equals(Object obj) {
        if (obj instanceof Connection) {
            return ((Connection) obj).getConnectionId().equals(this.getConnectionId());
        }
        return false;
    }

    @Override
    public String toString() {
        Set<Beacon> beaconSet = this.getBeacons();
        Beacon[] beacons = beaconSet.toArray(new Beacon[beaconSet.size()]);
        return "[Connection: " + getConnectionId() +
                ", Project: " + getProject().getProjectId() + "]";
    }
}
