package com.dteknoloji.controller;

/*
 * This code belongs to:
 * Ahmet Emre Unal
 * S001974
 * emre.unal@ozu.edu.tr
 */

import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@Table(name = "beacon_groups")
public class BeaconGroup {
    @Id
    @Column(name = "beacon_group_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long beaconGroupId;

    @Column(name = "name", nullable = false)
    @Size(min = 1, max = 50)
    private String name;

    @Column(name = "description", nullable = true)
    @Size(min = 0, max = 200)
    private String description;

    public Long getBeaconGroupId() {
        return beaconGroupId;
    }

    public void setBeaconGroupId(Long beaconGroupId) {
        this.beaconGroupId = beaconGroupId;
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
}
