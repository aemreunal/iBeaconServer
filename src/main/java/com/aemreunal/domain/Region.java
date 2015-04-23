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
import com.aemreunal.helper.ImageProperties;
import com.aemreunal.helper.json.JsonBuilderFactory;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "regions")
@ResponseBody
@JsonIgnoreProperties(value = { "beacons", "project", "mapImageFileName", "designatedBeacons" })
public class Region extends ResourceSupport implements Serializable, Comparable {
    public static final int NAME_MAX_LENGTH         = 50;
    public static final int DESCRIPTION_MAX_LENGTH  = 200;
    public static final int DISPLAY_NAME_MAX_LENGTH = 50;
    public static final int UUID_MAX_LENGTH         = 36; // UUID hex string (including dashes) is 36 characters long

    /*
     *------------------------------------------------------------
     * BEGIN: Region 'ID' attribute
     */
    @Id
    @Column(name = "region_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Access(AccessType.PROPERTY)
    private Long regionId;
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
    /*
     * END: Region 'description' attribute
     *------------------------------------------------------------
     */

    /*
     *------------------------------------------------------------
     * BEGIN: Beacon 'display name' attribute
     *
     * A 'display name' is a user-friendly text that can be displayed on a client as
     * the name of this region/location. For example, the region description may be
     * "AB2, Floor 4, North", which isn't a user-friendly way to describe a location.
     * This text can be "Student Center, 4th floor", which can be displayed to a user
     * to describe the area this region represents.
     */
    @Column(name = "display_name", nullable = false, length = DISPLAY_NAME_MAX_LENGTH)
    @Size(max = DISPLAY_NAME_MAX_LENGTH)
    @Access(AccessType.PROPERTY)
    private String displayName = "";
    /*
     * END: Beacon 'display name' attribute
     *------------------------------------------------------------
     */

    /*
     *------------------------------------------------------------
     * BEGIN: Region 'Map image' attribute
     *
     * The length is UUID_MAX_LENGTH, because image names are set as UUIDs.
     */
    @Column(name = "map_image_name", nullable = false, length = UUID_MAX_LENGTH)
    @Size(min = UUID_MAX_LENGTH, max = UUID_MAX_LENGTH)
    @Access(AccessType.PROPERTY)
    private String mapImageFileName = "";
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
    /*
     * END: Region 'region height' attribute
     *------------------------------------------------------------
     */

    /*
     *------------------------------------------------------------
     * BEGIN: Region 'beacons' attribute
     *
     * Currently, Beacon is the owner of its relationship to Region.
     * ManyToOne are (almost) always the owner side of a bidirectional relationship in the JPA spec
     *
     * Should Beacon even know/care about which region(s) it belongs to?
     *
     * Correct mapping: 2.2.5.3.1.1. Bidirectional
     * http://docs.jboss.org/hibernate/stable/annotations/reference/en/html/entity.html
     *
     * @JoinTable(name="region_members",
     *            joinColumns = @JoinColumn(name="region_id"),
     *            inverseJoinColumns = @JoinColumn(name="beacon_id"))
     *
     * TODO:XNYLXIWD determine who should own this relationship
     */
    @OneToMany(targetEntity = Beacon.class,
            mappedBy = "region",
            fetch = FetchType.LAZY,
            orphanRemoval = true,
            cascade = CascadeType.REMOVE)
    @Access(AccessType.PROPERTY)
    private Set<Beacon> beacons = new LinkedHashSet<Beacon>();
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
    @JoinTable(name = "projects_to_regions",
            joinColumns = @JoinColumn(name = "region_id"),
            inverseJoinColumns = @JoinColumn(name = "project_id"))
    @Access(AccessType.PROPERTY)
    private Project project;
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
//    @CreatedDate
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
//    @LastModifiedDate
    /*
     * END: Region 'lastUpdateDate' attribute
     *------------------------------------------------------------
     */

    /*
     *------------------------------------------------------------
     * BEGIN: Constructors
     */
    public Region() {
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

    public void markAsUpdated() {
        setLastUpdatedDate(new Date());
    }

    public JSONObject getQueryResponse() {
        return JsonBuilderFactory.object()
                                 .add("regionId", getRegionId())
                                 .add("displayName", getDisplayName())
                                 .add("regionWidth", getRegionWidth())
                                 .add("regionHeight", getRegionHeight())
                                 .add("lastUpdatedDate", getLastUpdatedDate())
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
    public Long getRegionId() {
        return regionId;
    }

    public void setRegionId(Long regionId) {
        this.regionId = regionId;
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

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getMapImageFileName() {
        return mapImageFileName;
    }

    public void setMapImageFileName(String mapImageFileName) {
        this.mapImageFileName = mapImageFileName;
    }

    public Integer getRegionWidth() {
        return regionWidth;
    }

    public void setRegionWidth(Integer regionWidth) {
        this.regionWidth = regionWidth;
    }

    public Integer getRegionHeight() {
        return regionHeight;
    }

    public void setRegionHeight(Integer regionHeight) {
        this.regionHeight = regionHeight;
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

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(Date lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }
    /*
     * END: Getters & Setters
     *------------------------------------------------------------
     */

    @PrePersist
    private void setInitialProperties() {
        // Set beacon creation date
        if (creationDate == null) {
            Date now = new Date();
            setCreationDate(now);
            setLastUpdatedDate(now);
        }
    }

    @Override
    public String toString() {
        return "[Region: " + getRegionId() + ", Project: "
                + getProject().getProjectId() + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Region) {
            return ((Region) obj).getRegionId().equals(this.getRegionId());
        }
        return false;
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof Region) {
            return this.getRegionId().compareTo(((Region) o).getRegionId());
        }
        return 0;
    }
}
