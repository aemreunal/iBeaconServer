package com.dteknoloji.domain;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.Size;
import org.springframework.hateoas.ResourceSupport;

@Entity
@Table(name = "beacons")
public class Beacon extends ResourceSupport implements Serializable {

    @Id
    @Column(name = "beacon_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long beaconId;

    @Column(name = "uuid", nullable = false)
    @Size(min = 36, max = 36)
    private String uuid;

    @Column(name = "major", nullable = false)
    @Size(min = 4, max = 4)
    private String major;

    @Column(name = "minor", nullable = false)
    @Size(min = 4, max = 4)
    private String minor;

    @ManyToOne(targetEntity = BeaconGroup.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = true)
    @JoinColumn(name = "group_id", referencedColumnName = "beacon_group_id")
    private Long groupId;

    @Column(name = "description", nullable = true)
    @Size(min = 0, max = 200)
    private String description;

    public Long getBeaconId() {
        return beaconId;
    }

    public void setBeaconId(Long beaconId) {
        this.beaconId = beaconId;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getMinor() {
        return minor;
    }

    public void setMinor(String minor) {
        this.minor = minor;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
