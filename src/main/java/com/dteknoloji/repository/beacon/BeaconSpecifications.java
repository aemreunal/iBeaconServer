package com.dteknoloji.repository.beacon;

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
import com.dteknoloji.domain.Beacon;

public class BeaconSpecifications {
    /**
     * Creates the Beacon search specification from the given attributes
     *
     * @param uuid
     *     The UUID attribute to search for
     * @param major
     *     The major attribute to search for
     * @param minor
     *     The minor attribute to search for
     *
     * @return The specification of the beacon
     */
    public static Specification<Beacon> beaconWithSpecification(final String uuid, final String major, final String minor) {
        return new Specification<Beacon>() {
            public Predicate toPredicate(Root<Beacon> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                ArrayList<Predicate> predicates = new ArrayList<Predicate>();

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
}
