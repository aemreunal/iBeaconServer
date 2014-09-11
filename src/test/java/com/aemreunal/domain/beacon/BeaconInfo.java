package com.aemreunal.domain.beacon;

import java.util.HashMap;
import com.jayway.restassured.path.json.JsonPath;

/*
 ***************************
 * Copyright (c) 2014      *
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
 ***************************
 */

/*
 * Request body:
 * {
 *     "uuid":"xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx",
 *     "major":"1",
 *     "minor":"2",
 *     "description":"Test beacon"
 * }
 *
 * Response body:
 * {
 *     "beaconId": 1,
 *     "creationDate": 1408536324312,
 *     "description": "Test beacon",
 *     "links": [],
 *     "major": "1",
 *     "minor": "2",
 *     "uuid": "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"
 * }
 */

public class BeaconInfo {
    public String ownerUsername = "";
    public Long   projectId     = (long) -1;
    public Long   beaconId      = (long) -1;
    public String uuid          = "";
    public String major         = "";
    public String minor         = "";
    public String description   = "";

    public BeaconInfo(String ownerUsername, Long projectId, Long beaconId, String uuid, String major, String minor, String description) {
        this.ownerUsername = ownerUsername;
        this.projectId = projectId;
        this.beaconId = beaconId;
        this.uuid = uuid;
        this.major = major;
        this.minor = minor;
        this.description = description;
    }

    public BeaconInfo(JsonPath beaconJson, Long projectId, String ownerUsername) {
        this.projectId = projectId;
        this.ownerUsername = ownerUsername;
        this.beaconId = beaconJson.getLong("beaconId");
        this.uuid = beaconJson.getString("uuid");
        this.major = beaconJson.getString("major");
        this.minor = beaconJson.getString("minor");
        this.description = beaconJson.getString("description");
    }

    public BeaconInfo(HashMap<String, Object> beaconMap, Long projectId, String ownerUsername) {
        this.projectId = projectId;
        this.ownerUsername = ownerUsername;
        this.beaconId = Long.parseLong(beaconMap.get("beaconId").toString());
        this.uuid = beaconMap.get("uuid").toString();
        this.major = beaconMap.get("major").toString();
        this.minor = beaconMap.get("minor").toString();
        this.description = beaconMap.get("description").toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof BeaconInfo)) {
            return false;
        } else {
            BeaconInfo otherBeaconInfo = (BeaconInfo) obj;
            boolean beaconIdMatches = otherBeaconInfo.beaconId.equals(this.beaconId);
            boolean uuidMatches = otherBeaconInfo.uuid.toUpperCase().equals(this.uuid.toUpperCase());
            boolean majorMatches = otherBeaconInfo.major.toUpperCase().equals(this.major.toUpperCase());
            boolean minorMatches = otherBeaconInfo.minor.toUpperCase().equals(this.minor.toUpperCase());
            boolean descriptionMatches = otherBeaconInfo.description.equals(this.description);
            return beaconIdMatches && uuidMatches && majorMatches && minorMatches && descriptionMatches;
        }
    }
}
