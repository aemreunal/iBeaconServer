package com.aemreunal.repository.connection;

/*
 * *********************** *
 * Copyright (c) 2015      *
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
 * *********************** *
 */


import java.util.ArrayList;
import java.util.Set;
import javax.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import com.aemreunal.domain.Beacon;
import com.aemreunal.domain.Connection;

public class ConnectionSpecs {
    public static Specification<Connection> connectionWithSpecification(final Long projectId, final Beacon beaconOne, final Beacon beaconTwo) {
        return (root, query, builder) -> {
            ArrayList<Predicate> predicates = new ArrayList<Predicate>();
            // Project specification
            predicates.add(builder.equal(root.get("project").get("projectId"), projectId));

            if (beaconOne != null) {
                predicates.add(builder.isMember(beaconOne, root.get("beacons").as(Set.class)));
            }

            if (beaconTwo != null) {
                predicates.add(builder.isMember(beaconTwo, root.get("beacons").as(Set.class)));
            }

            return builder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }

}
