package com.aemreunal.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.Size;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.web.bind.annotation.ResponseBody;
import com.aemreunal.helper.ImageProperties;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/*
 **************************
 * Copyright (c) 2015     *
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
@Table(name = "regions")
@ResponseBody
@JsonIgnoreProperties(value = { "beacons", "project", "mapImageFileName", "designatedBeacons" })
public class Region extends ResourceSupport implements Serializable {
    public static final int NAME_MAX_LENGTH        = 50;
    public static final int DESCRIPTION_MAX_LENGTH = 200;

    /*
     *------------------------------------------------------------
     * BEGIN: Region 'ID' attribute
     */
    @Id
    @Column(name = "region_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @OrderColumn
    @Access(AccessType.PROPERTY)
    private Long regionId;

    public Long getRegionId() {
        return regionId;
    }

    public void setRegionId(Long regionId) {
        this.regionId = regionId;
    }
    /*
     * END: Region 'ID' attribute
     *------------------------------------------------------------
     */

    /*
     *------------------------------------------------------------
     * BEGIN: Region 'name' attribute
     */
    @Column(name = "name", nullable = false, length = NAME_MAX_LENGTH)
    @Size(min = 1, max = NAME_MAX_LENGTH)
    @Access(AccessType.PROPERTY)
    private String name = "";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    /*
     * END: Region 'name' attribute
     *------------------------------------------------------------
     */

    /*
     *------------------------------------------------------------
     * BEGIN: Region 'description' attribute
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
     * END: Region 'description' attribute
     *------------------------------------------------------------
     */

    /*
     *------------------------------------------------------------
     * BEGIN: Region 'Map image' attribute
     */
    @Column(name = "map_image_name", nullable = false)
    @Access(AccessType.PROPERTY)
    private String mapImageFileName = "";

    public String getMapImageFileName() {
        return mapImageFileName;
    }

    public void setMapImageFileName(String mapImageFileName) {
        this.mapImageFileName = mapImageFileName;
    }

    @JsonSerialize
    public boolean mapImageIsSet() {
        return getMapImageFileName() != null && !getMapImageFileName().isEmpty();
    }
    /*
     * END: Region 'Map image' attribute
     *------------------------------------------------------------
     */

    /*
     *------------------------------------------------------------
     * BEGIN: Region 'region width' attribute
     *
     * Represents region width in width of the image as pixels.
     */
    @Column(name = "region_width", nullable = false)
    @Access(AccessType.PROPERTY)
    private Integer regionWidth = 0;

    public Integer getRegionWidth() {
        return regionWidth;
    }

    public void setRegionWidth(Integer regionWidth) {
        this.regionWidth = regionWidth;
    }
    /*
     * END: Region 'region width' attribute
     *------------------------------------------------------------
     */

    /*
     *------------------------------------------------------------
     * BEGIN: Region 'region height' attribute
     *
     * Represents region height in height of the image as pixels.
     */
    @Column(name = "region_height", nullable = false)
    @Access(AccessType.PROPERTY)
    private Integer regionHeight = 0;

    public Integer getRegionHeight() {
        return regionHeight;
    }

    public void setRegionHeight(Integer regionHeight) {
        this.regionHeight = regionHeight;
    }
    /*
     * END: Region 'region height' attribute
     *------------------------------------------------------------
     */

    /*
     *------------------------------------------------------------
     * BEGIN: Region 'beacons' attribute
     */

    /*
     *
     * Currently, Beacon is the owner of its relationship to Region.
     * ManyToOne are (almost) always the owner side of a bidirectional relationship in the JPA spec
     *
     * Should Beacon even know/care about which region(s) it belongs to?
     *
     * Correct mapping: 2.2.5.3.1.1. Bidirectional
     * http://docs.jboss.org/hibernate/stable/annotations/reference/en/html/entity.html
     *
     @JoinTable(name="region_members",
                joinColumns = @JoinColumn(name="region_id"),
                inverseJoinColumns = @JoinColumn(name="beacon_id")
     )
     // TODO:XNYLXIWD determine who should own this relationship
     */
    @OneToMany(targetEntity = Beacon.class,
            mappedBy = "region",
            fetch = FetchType.LAZY,
            orphanRemoval = true,
            cascade = CascadeType.REMOVE)
    @Access(AccessType.PROPERTY)
    private Set<Beacon> beacons = new LinkedHashSet<Beacon>();

    public Set<Beacon> getBeacons() {
        return beacons;
    }

    public void setBeacons(Set<Beacon> beacons) {
        this.beacons = beacons;
    }
    /*
     * END: Region 'beacons' attribute
     *------------------------------------------------------------
     */

    /*
     *------------------------------------------------------------
     * BEGIN: Region 'project' attribute
     */
    @ManyToOne(targetEntity = Project.class,
            optional = false,
            fetch = FetchType.LAZY)
    // JoinTable & Lazy fetch-> 5.1.7: http://docs.jboss.org/hibernate/core/4.3/manual/en-US/html_single/
    @JoinTable(name = "projects_to_regions",
            joinColumns = @JoinColumn(name = "region_id"),
            inverseJoinColumns = @JoinColumn(name = "project_id"))
    @Access(AccessType.PROPERTY)
    private Project project;

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }
    /*
     * END: Region 'project' attribute
     *------------------------------------------------------------
     */

    /*
     *------------------------------------------------------------
     * BEGIN: Region 'creationDate' attribute
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
     * END: Region 'creationDate' attribute
     *------------------------------------------------------------
     */

    /*
     *------------------------------------------------------------
     * BEGIN: Region 'lastUpdateDate' attribute
     */
    @Column(name = "last_update_date", nullable = false)
    @Access(AccessType.PROPERTY)
    private Date lastUpdatedDate = null;

    public Date getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(Date lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }

    public void markAsUpdated() {
        setLastUpdatedDate(new Date());
    }
    /*
     * END: Region 'lastUpdateDate' attribute
     *------------------------------------------------------------
     */

//    @LastModifiedDate
//    @CreatedDate

    @PrePersist
    private void setInitialProperties() {
        // Set beacon creation date
        if (creationDate == null) {
            Date now = new Date();
            setCreationDate(now);
            setLastUpdatedDate(now);
        }
    }

    public void setImageProperties(ImageProperties imageProperties) {
        this.setMapImageFileName(imageProperties.getImageFileName());
        this.setRegionWidth(imageProperties.getImageWidth());
        this.setRegionHeight(imageProperties.getImageHeight());
    }

    public boolean beaconCoordsAreValid(Beacon beacon) {
        Integer beaconX = beacon.getxCoordinate();
        Integer beaconY = beacon.getyCoordinate();
        boolean xIsValid = beaconX >= 0 && beaconX < this.getRegionWidth();
        boolean yIsValid = beaconY >= 0 && beaconY < this.getRegionHeight();
        return xIsValid && yIsValid;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Region)
                && ((Region) obj).getRegionId().equals(this.getRegionId());
    }
}
