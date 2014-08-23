package com.aemreunal.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.Size;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.web.bind.annotation.ResponseBody;
import com.aemreunal.config.CoreConfig;
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
    public static final int NAME_MAX_LENGTH = 100;
    public static final int DESCRIPTION_MAX_LENGTH = 1000;

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
               inverseJoinColumns = @JoinColumn(name="project_id"))
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
    /*
     * END: Scenario 'beacon groups list' attribute
     *------------------------------------------------------------
     */


    /*
     *------------------------------------------------------------
     * BEGIN: Scenario 'creationDate' attribute
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
     * END: Scenario 'creationDate' attribute
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
    public boolean equals(Object obj) {
        if (!(obj instanceof Scenario)) {
            return false;
        } else {
            return ((Scenario) obj).getScenarioId().equals(this.getScenarioId());
        }
    }
}
