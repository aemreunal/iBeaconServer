package com.aemreunal.repository.beacon;

/*
 **************************
 * Copyright (c) 2015     *
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

import java.util.ArrayList;
import javax.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import com.aemreunal.domain.Beacon;

public class BeaconSpecs {
    /**
     * Creates the Beacon search specification from the given attributes
     *
     * @param username
     *         The username to filter the results by. Could be NULL for API queries.
     * @param projectId
     *         The project ID to filter the results by. Could be NULL for API queries.
     * @param regionId
     *         The region ID to filter the results by. Could be NULL for API queries.
     * @param uuid
     *         The UUID attribute to filter the results by.
     * @param major
     *         The major attribute to filter the results by.
     * @param minor
     *         The minor attribute to filter the results by.
     * @param designated
     *         The designated attribute to filter the results by.
     *
     * @return The specification of the beacon
     */
    public static Specification<Beacon> beaconWithSpecification(final String username, final Long projectId, final Long regionId, final String uuid, final Integer major, final Integer minor, final Boolean designated) {
        return (root, query, builder) -> {
            ArrayList<Predicate> predicates = new ArrayList<Predicate>();

            if (username != null) {
                predicates.add(builder.equal(root.get("region").get("project").get("owner").get("username").as(String.class), username));
            }

            if (projectId != null) {
                predicates.add(builder.equal(root.get("region").get("project").get("projectId"), projectId));
            }

            if (regionId != null) {
                predicates.add(builder.equal(root.get("region").get("regionId"), regionId));
            }

            if (uuid != null && !uuid.equals("")) {
                if (uuid.length() == Beacon.UUID_MAX_LENGTH) {
                    predicates.add(builder.equal(root.get("uuid"), uuid.toUpperCase()));
                } else {
                    predicates.add(builder.like(root.get("uuid").as(String.class), "%" + uuid.toUpperCase() + "%"));
                }
            }

            if (major != null && !major.equals(-1)) {
                predicates.add(builder.equal(root.get("major"), major));
            }

            if (minor != null && !minor.equals(-1)) {
                predicates.add(builder.equal(root.get("minor"), minor));
            }

            if (designated != null) {
                predicates.add(builder.equal(root.get("designated").as(Boolean.class), designated));
            }

            return builder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}
