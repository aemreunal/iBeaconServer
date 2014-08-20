package com.aemreunal.repository.beaconGroup;


import java.util.ArrayList;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import com.aemreunal.domain.BeaconGroup;

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
public class BeaconGroupSpecs {
    public static Specification<BeaconGroup> beaconGroupWithSpecification(final Long projectId, final String name) {
        return new Specification<BeaconGroup>() {
            public Predicate toPredicate(Root<BeaconGroup> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                ArrayList<Predicate> predicates = new ArrayList<Predicate>();
                // Project specification
                predicates.add(builder.equal(root.get("project").get("projectId"), projectId));

                if (!name.equals("")) {
                    predicates.add(builder.like(root.get("name").as(String.class), "%" + name + "%"));
                }

                return builder.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
    }

    /**
     * Creates the 'Beacon group exists' search specification from the given attributes
     *
     * @param projectId
     *     The project ID to search in
     * @param beaconGroupId
     *     The ID of the beacon group to find
     *
     * @return The specification of the beacon group
     */
    public static Specification<BeaconGroup> beaconGroupExistsSpecification(final Long projectId, final Long beaconGroupId) {
        return new Specification<BeaconGroup>() {
            public Predicate toPredicate(Root<BeaconGroup> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                ArrayList<Predicate> predicates = new ArrayList<Predicate>();
                // Project specification
                predicates.add(builder.equal(root.get("project").get("projectId"), projectId));

                // Beacon specification
                predicates.add(builder.equal(root.get("beaconGroupId"), beaconGroupId));

                return builder.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
    }
}
