package com.dteknoloji.domain;

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

    @Column(name = "name", nullable = false, length = 50)
    @Size(min = 1, max = 50)
    private String name;

    @Column(name = "description", nullable = true, length = 200)
    @Size(min = 0, max = 200)
    private String description;

//    @OneToMany(targetEntity = Beacon.class, mappedBy = "groupId")
//    // TODO Cascade? When group is deleted, associated beacon group IDs must be updated as well
//    // TODO Cascade for whole projects: http://examples.javacodegeeks.com/enterprise-java/hibernate/hibernate-cascade-example/
//    private Collection<Beacon> members;

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
