package com.aemreunal.repository.beacon;

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

import java.util.ArrayList;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import com.aemreunal.domain.Beacon;

public class BeaconSpecs {
    /**
     * Creates the Beacon search specification from the given attributes
     *
     * @param projectId
     *     The project ID to search in
     * @param uuid
     *     The UUID attribute to search for
     * @param major
     *     The major attribute to search for
     * @param minor
     *     The minor attribute to search for
     *
     * @return The specification of the beacon
     */
    public static Specification<Beacon> beaconWithSpecification(final Long projectId, final String uuid, final String major, final String minor) {
        return new Specification<Beacon>() {
            public Predicate toPredicate(Root<Beacon> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                ArrayList<Predicate> predicates = new ArrayList<Predicate>();
                predicates.add(builder.equal(root.get("project").get("projectId"), projectId));

                if (!uuid.equals("")) {
                    if (uuid.length() == Beacon.UUID_MAX_LENGTH) {
                        predicates.add(builder.equal(root.get("uuid"), uuid.toUpperCase()));
                    } else {
                        predicates.add(builder.like(root.get("uuid").as(String.class), "%" + uuid.toUpperCase() + "%"));
                    }
                }

                if (!major.equals("")) {
                    predicates.add(builder.equal(root.get("major"), major.toUpperCase()));
                }

                if (!minor.equals("")) {
                    predicates.add(builder.equal(root.get("minor"), minor.toUpperCase()));
                }

                return builder.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
    }

    /**
     * Creates the 'Beacon exists' search specification from the given attributes
     *
     * @param projectId
     *     The project ID to search in
     * @param beaconId
     *     The ID of the beacon to find
     *
     * @return The specification of the beacon
     */
    public static Specification<Beacon> beaconExistsSpecification(final Long projectId, final Long beaconId) {
        return new Specification<Beacon>() {
            public Predicate toPredicate(Root<Beacon> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                ArrayList<Predicate> predicates = new ArrayList<Predicate>();
                // Project specification
                predicates.add(builder.equal(root.get("project").get("projectId"), projectId));

                // Beacon specification
                predicates.add(builder.equal(root.get("beaconId"), beaconId));

                return builder.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
    }
}
