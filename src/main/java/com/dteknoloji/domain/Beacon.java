package com.dteknoloji.domain;

import java.io.Serializable;
import javax.persistence.*;
import org.springframework.hateoas.ResourceSupport;

@Entity
@Table(name = "beacons")
public class Beacon extends ResourceSupport implements Serializable {

    @Id
    @Column(name = "beacon_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long beaconId;

    @Column(name = "uuid", nullable = false)
    private String uuid;

    @Column(name = "major", nullable = false)
    private String major;

    @Column(name = "minor", nullable = false)
    private String minor;

//    @JsonIgnore
//    @Column(name = "GROUP_ID", nullable = true)
//    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//    @JoinColumn(name = "GROUP_ID", nullable = true)
//    private Long groupId;

    @Column(name = "description", nullable = true)
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

//    public Long getGroupId() {
//        return groupId;
//    }
//
//    public void setGroupId(Long groupId) {
//        this.groupId = groupId;
//    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
