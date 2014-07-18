package com.dteknoloji.domain;

import javax.persistence.*;

/*
 * A proximity UUID, which is a 128-bit value
 * A major value, which is a 16-bit unsigned integer
 * A minor value, which is a 16-bit unsigned integer
 */

@Entity
@Table(name = "BEACONS")
public class Beacon {
    @Id
    @Column(name="ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private double id;

    @Column(name="UUID")
    private String uuid;

//    @ManyToOne(mappedBy="ID", fetch = FetchType.LAZY, cascade=CascadeType.ALL)
    @Column(name="GROUP")
    private double group;

    @Column(name="MAJOR")
    private int major;

    @Column(name="MINOR")
    private int minor;
}
