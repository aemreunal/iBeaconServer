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
import com.aemreunal.helper.json.JsonBuilderFactory;
import com.aemreunal.helper.json.JsonObjectBuilder;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "scenarios")
@ResponseBody
@JsonIgnoreProperties(value = { "beacons", "project" })
public class Scenario extends ResourceSupport implements Serializable, Comparable {
    public static final int NAME_MAX_LENGTH          = 100;
    public static final int DESCRIPTION_MAX_LENGTH   = 1000;
    public static final int MESSAGE_SHORT_MAX_LENGTH = 100;
    public static final int MESSAGE_LONG_MAX_LENGTH  = 1000;
    public static final int URL_MAX_LENGTH           = 500;

    /*
     *------------------------------------------------------------
     * BEGIN: Scenario 'ID' attribute
     */
    @Id
    @Column(name = "scenario_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Access(AccessType.PROPERTY)
    private Long scenarioId;
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
    /*
     * END: Scenario 'beacons list' attribute
     *------------------------------------------------------------
     */

    /*
     *------------------------------------------------------------
     * BEGIN: Scenario 'creationDate' attribute
     */
    @Column(name = "creation_date", nullable = false)
    @Access(AccessType.PROPERTY)
    private Date creationDate = null;
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
    /*
     * END: Scenario 'url' attribute
     *------------------------------------------------------------
     */

    /*
     *------------------------------------------------------------
     * BEGIN: Constructors
     */
    public Scenario() {
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
    public JSONObject generateQueryResponse() {
        JsonObjectBuilder builder = JsonBuilderFactory.object();
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

    public boolean hasShortMessage() {
        return !(messageShort.equals(""));
    }

    public boolean hasLongMessage() {
        return !(messageLong.equals(""));
    }

    public boolean hasUrl() {
        return !(url.equals(""));
    }
    /*
     * END: Helpers
     *------------------------------------------------------------
     */

    /*
     *------------------------------------------------------------
     * BEGIN: Getters & Setters
     */
    public Long getScenarioId() {
        return scenarioId;
    }

    public void setScenarioId(Long scenarioId) {
        this.scenarioId = scenarioId;
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

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Set<Beacon> getBeacons() {
        return beacons;
    }

    public void setBeacons(Set<Beacon> beacons) {
        this.beacons = beacons;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getMessageShort() {
        return messageShort;
    }

    public void setMessageShort(String messageShort) {
        this.messageShort = messageShort;
    }

    public String getMessageLong() {
        return messageLong;
    }

    public void setMessageLong(String messageLong) {
        this.messageLong = messageLong;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    /*
     * END: Getters & Setters
     *------------------------------------------------------------
     */

    @PrePersist
    private void setInitialProperties() {
        // Set scenario creation date
        if (creationDate == null) {
            setCreationDate(new Date());
        }
    }

    @Override
    public String toString() {
        return "[Scenario: " + getScenarioId() +
                ", Project: " + getProject().getProjectId() + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Scenario) {
            return ((Scenario) obj).getScenarioId().equals(this.getScenarioId());
        }
        return false;
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof Scenario) {
            return this.getScenarioId().compareTo(((Scenario) o).getScenarioId());
        }
        return 0;
    }
}
