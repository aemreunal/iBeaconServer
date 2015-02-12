package com.aemreunal.repository.region;


import java.util.ArrayList;
import javax.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import com.aemreunal.domain.Region;

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
public class RegionSpecs {
    public static Specification<Region> regionWithSpecification(final Long projectId, final String name) {
        return (root, query, builder) -> {
            ArrayList<Predicate> predicates = new ArrayList<Predicate>();
            // Project specification
            predicates.add(builder.equal(root.get("project").get("projectId"), projectId));

            if (!name.equals("")) {
                predicates.add(builder.like(root.get("name").as(String.class), "%" + name + "%"));
            }

            return builder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }

    /**
     * Creates the 'region exists' search specification from the given attributes
     *
     * @param projectId
     *     The project ID to search in
     * @param regionId
     *     The ID of the region to find
     *
     * @return The specification of the region
     */
    public static Specification<Region> regionExistsSpecification(final Long projectId, final Long regionId) {
        return (root, query, builder) -> {
            ArrayList<Predicate> predicates = new ArrayList<Predicate>();
            // Project specification
            predicates.add(builder.equal(root.get("project").get("projectId"), projectId));

            // Beacon specification
            predicates.add(builder.equal(root.get("regionId"), regionId));

            return builder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}
