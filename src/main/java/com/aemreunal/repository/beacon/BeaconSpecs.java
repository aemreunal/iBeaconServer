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
     *         The username to search for. Could be NULL for API queries.
     * @param projectId
     *         The project ID to search for. Could be NULL for API queries.
     * @param regionId
     *         The region ID to search for. Could be NULL for API queries.
     * @param uuid
     *         The UUID attribute to search for.
     * @param major
     *         The major attribute to search for.
     * @param minor
     *         The minor attribute to search for.
     *
     * @return The specification of the beacon
     */
    public static Specification<Beacon> beaconWithSpecification(final String username, final Long projectId, final Long regionId, final String uuid, final Integer major, final Integer minor) {
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

            if (!uuid.equals("")) {
                if (uuid.length() == Beacon.UUID_MAX_LENGTH) {
                    predicates.add(builder.equal(root.get("uuid"), uuid.toUpperCase()));
                } else {
                    predicates.add(builder.like(root.get("uuid").as(String.class), "%" + uuid.toUpperCase() + "%"));
                }
            }

            if (!major.equals(-1)) {
                predicates.add(builder.equal(root.get("major"), major));
            }

            if (!minor.equals(-1)) {
                predicates.add(builder.equal(root.get("minor"), minor));
            }

            return builder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }

    /**
     * Creates the 'Beacon exists' search specification from the given attributes
     *
     * @param regionId
     *         The region ID to search in
     * @param beaconId
     *         The ID of the beacon to find
     *
     * @return The specification of the beacon
     */
    public static Specification<Beacon> beaconExistsSpecification(final Long regionId, final Long beaconId) {
        return (root, query, builder) -> {
            ArrayList<Predicate> predicates = new ArrayList<Predicate>();
            // Region specification
            predicates.add(builder.equal(root.get("region").get("regionId"), regionId));

            // Beacon specification
            predicates.add(builder.equal(root.get("beaconId"), beaconId));

            return builder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}
