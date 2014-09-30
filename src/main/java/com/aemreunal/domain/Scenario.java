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
@Table(name = "scenarios")
@ResponseBody
@JsonIgnoreProperties(value = { "beacons", "project", "beaconGroups" })
public class Scenario extends ResourceSupport implements Serializable {
    public static final int NAME_MAX_LENGTH          = 100;
    public static final int DESCRIPTION_MAX_LENGTH   = 1000;
    public static final int MESSAGE_SHORT_MAX_LENGTH = 100;
    public static final int MESSAGE_LONG_MAX_LENGTH  = 1000;
    public static final int URL_MAX_LENGTH  = 500;

    /*
     *------------------------------------------------------------
     * BEGIN: Scenario 'ID' attribute
     */
    @Id
    @Column(name = "scenario_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @OrderColumn
    @Access(AccessType.PROPERTY)
    private Long scenarioId;

    public Long getScenarioId() {
        return scenarioId;
    }

    public void setScenarioId(Long scenarioId) {
        this.scenarioId = scenarioId;
    }
    /*
     * END: Scenario 'ID' attribute
     *------------------------------------------------------------
     */

    /*
     *------------------------------------------------------------
     * BEGIN: Scenario 'name' attribute
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
     * END: Scenario 'name' attribute
     *------------------------------------------------------------
     */

    /*
     *------------------------------------------------------------
     * BEGIN: Scenario 'description' attribute
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
     * END: Scenario 'description' attribute
     *------------------------------------------------------------
     */

    /*
     *------------------------------------------------------------
     * BEGIN: Scenario 'project' attribute
     */
    @ManyToOne(targetEntity = Project.class,
               optional = false,
               fetch = FetchType.LAZY)
    @JoinTable(name = "projects_to_scenarios",
               joinColumns = @JoinColumn(name = "scenario_id"),
               inverseJoinColumns = @JoinColumn(name = "project_id"))
    @Access(AccessType.PROPERTY)
    private Project project;

    public Project getProject() {
        CoreConfig.initLazily(project);
        return project;
    }

    public void setProject(Project project) {
        CoreConfig.initLazily(project);
        this.project = project;
    }
    /*
     * END: Scenario 'project' attribute
     *------------------------------------------------------------
     */

    /*
     *------------------------------------------------------------
     * BEGIN: Scenario 'beacons list' attribute
     */
    @OneToMany(targetEntity = Beacon.class,
               mappedBy = "scenario",
               fetch = FetchType.LAZY)
    @Access(AccessType.PROPERTY)
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

    public void removeBeacon(Beacon beacon) {
        CoreConfig.initLazily(beacons);
        this.beacons.remove(beacon);
    }
    /*
     * END: Scenario 'beacons list' attribute
     *------------------------------------------------------------
     */

    /*
     *------------------------------------------------------------
     * BEGIN: Scenario 'beacon groups list' attribute
     */
    @OneToMany(targetEntity = BeaconGroup.class,
               mappedBy = "scenario",
               fetch = FetchType.LAZY)
    @OrderBy(value = "beaconGroupId")
    @Access(AccessType.PROPERTY)
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

    public void removeBeaconGroup(BeaconGroup beaconGroup) {
        CoreConfig.initLazily(beaconGroups);
        this.beaconGroups.remove(beaconGroup);
    }
    /*
     * END: Scenario 'beacon groups list' attribute
     *------------------------------------------------------------
     */

    /*
     *------------------------------------------------------------
     * BEGIN: Scenario 'creationDate' attribute
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
     * END: Scenario 'creationDate' attribute
     *------------------------------------------------------------
     */

    /*
     *------------------------------------------------------------
     * BEGIN: Scenario 'messageShort' attribute
     *
     * This value is to be used as the notification string.
     */
    @Column(name = "message_short", nullable = false, length = MESSAGE_SHORT_MAX_LENGTH)
    @Size(min = 0, max = MESSAGE_SHORT_MAX_LENGTH)
    @Access(AccessType.PROPERTY)
    private String messageShort = "";

    public String getMessageShort() {
        return messageShort;
    }

    public void setMessageShort(String messageShort) {
        this.messageShort = messageShort;
    }

    public boolean hasShortMessage() {
        return !(messageShort.equals(""));
    }
    /*
     * END: Scenario 'messageShort' attribute
     *------------------------------------------------------------
     */

    /*
     *------------------------------------------------------------
     * BEGIN: Scenario 'messageLong' attribute
     *
     * This value is to be used as the notification string.
     */
    @Column(name = "message_long", nullable = false, length = MESSAGE_LONG_MAX_LENGTH)
    @Size(min = 0, max = MESSAGE_LONG_MAX_LENGTH)
    @Access(AccessType.PROPERTY)
    private String messageLong = "";

    public String getMessageLong() {
        return messageLong;
    }

    public void setMessageLong(String messageLong) {
        this.messageLong = messageLong;
    }

    public boolean hasLongMessage() {
        return !(messageLong.equals(""));
    }
    /*
     * END: Scenario 'messageLong' attribute
     *------------------------------------------------------------
     */

    /*
     *------------------------------------------------------------
     * BEGIN: Scenario 'url' attribute
     *
     * This value is to be used as the notification string.
     */
    @Column(name = "url", nullable = false, length = URL_MAX_LENGTH)
    @Size(min = 0, max = URL_MAX_LENGTH)
    @Access(AccessType.PROPERTY)
    private String url = "";

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean hasUrl() {
        return !(url.equals(""));
    }
    /*
     * END: Scenario 'url' attribute
     *------------------------------------------------------------
     */

    public JSONObject generateQueryResponse() {
        JsonBuilder builder = new JsonBuilder();
        if (hasShortMessage()) {
            builder = builder.add("short", getMessageShort());
        }
        if (hasLongMessage()) {
            builder = builder.add("long", getMessageLong());
        }
        if (hasUrl()) {
            builder = builder.add("url", getUrl());
        }
        return builder.build();
    }

    @PrePersist
    private void setInitialProperties() {
        // Set scenario creation date
        if (creationDate == null) {
            setCreationDate(new Date());
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Scenario)) {
            return false;
        } else {
            return ((Scenario) obj).getScenarioId().equals(this.getScenarioId());
        }
    }
}
