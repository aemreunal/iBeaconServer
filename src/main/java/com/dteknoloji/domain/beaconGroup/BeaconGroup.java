package com.dteknoloji.domain.beaconGroup;

/*
 * This code belongs to:
 * Ahmet Emre Unal
 * S001974
 * emre.unal@ozu.edu.tr
 */

import java.util.List;
import javax.persistence.*;
import javax.validation.constraints.Size;
import org.springframework.web.bind.annotation.ResponseBody;
import com.dteknoloji.domain.beacon.Beacon;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "beacon_groups")
@ResponseBody
@JsonIgnoreProperties(value = {"links"})
public class BeaconGroup {
    public static final int NAME_MAX_LENGTH = 50;
    public static final int DESCRIPTION_MAX_LENGTH = 200;

    @Id
    @Column(name = "beacon_group_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long beaconGroupId;

    @Column(name = "name", nullable = false, length = NAME_MAX_LENGTH)
    @Size(min = 1, max = NAME_MAX_LENGTH)
    private String name;

    @Column(name = "description", nullable = true, length = DESCRIPTION_MAX_LENGTH)
    @Size(min = 0, max = DESCRIPTION_MAX_LENGTH)
    private String description;

    // TODO Cascade? When group is deleted, associated beacon group IDs must be updated as well
    // TODO Cascade for whole projects: http://examples.javacodegeeks.com/enterprise-java/hibernate/hibernate-cascade-example/
    @JsonIgnore
    @OneToMany(targetEntity = Beacon.class, mappedBy = "group", fetch = FetchType.EAGER)
    @JsonIgnoreProperties(value = {"uuid", "major", "minor", "description", "group"})
    private List<Beacon> beacons;

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

    public List<Beacon> getBeacons() {
        return beacons;
    }

    public void setBeacons(List<Beacon> beacons) {
        this.beacons = beacons;
    }
}
