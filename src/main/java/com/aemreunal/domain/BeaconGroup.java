package com.aemreunal.domain;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.Size;
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
@Table(name = "beacon_groups")
@ResponseBody
@JsonIgnoreProperties(value = { "links" })
public class BeaconGroup {
    public static final int NAME_MAX_LENGTH = 50;
    public static final int DESCRIPTION_MAX_LENGTH = 200;

    /*
     *------------------------------------------------------------
     * BEGIN: Beacon group 'ID' attribute
     */
    @Id
    @Column(name = "beacon_group_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long beaconGroupId;

    public Long getBeaconGroupId() {
        return beaconGroupId;
    }

    public void setBeaconGroupId(Long beaconGroupId) {
        this.beaconGroupId = beaconGroupId;
    }
    /*
     * END: Beacon group 'ID' attribute
     *------------------------------------------------------------
     */

    /*
     *------------------------------------------------------------
     * BEGIN: Beacon group 'name' attribute
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
     * END: Beacon group 'name' attribute
     *------------------------------------------------------------
     */

    /*
     *------------------------------------------------------------
     * BEGIN: Beacon group 'description' attribute
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
     * END: Beacon group 'description' attribute
     *------------------------------------------------------------
     */

    /*
     *------------------------------------------------------------
     * BEGIN: Beacon group 'beacon list' attribute
     */
    @OneToMany(targetEntity = Beacon.class,
        mappedBy = "group",
        fetch = FetchType.EAGER)
    @JsonIgnoreProperties(value = { "uuid", "major", "minor", "description", "group", "project", "creationDate" })
    private Set<Beacon> beacons = new LinkedHashSet<Beacon>();

    public Set<Beacon> getBeacons() {
        return beacons;
    }

    public void setBeacons(Set<Beacon> beacons) {
        this.beacons = beacons;
    }

    public void addBeacon(Beacon beacon) {
        this.beacons.add(beacon);
    }

    public void removeBeacon(Beacon beacon) {
        this.beacons.remove(beacon);
    }
    /*
     * END: Beacon group 'beacon list' attribute
     *------------------------------------------------------------
     */

    /*
     *------------------------------------------------------------
     * BEGIN: Beacon group 'project' attribute
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
     * END: Beacon group 'project' attribute
     *------------------------------------------------------------
     */


    /*
     *------------------------------------------------------------
     * BEGIN: Beacon group 'creationDate' attribute
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
     * END: Beacon group 'creationDate' attribute
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
        if(!(obj instanceof BeaconGroup)) {
            return false;
        } else {
            return ((BeaconGroup) obj).getBeaconGroupId().equals(this.getBeaconGroupId());
        }
    }
}
