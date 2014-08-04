package com.aemreunal.repository.project;

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
import com.aemreunal.domain.Project;

public class ProjectSpecifications {

    /**
     * Creates the Project search specification from the given attributes
     *
     * @param projectName
     *     The Project Name attribute to search for
     * @param ownerName
     *     The Owner name attribute to search for
     * @param ownerID
     *     The Owner ID attribute to search for
     *
     * @return The specification of the project
     */
    public static Specification<Project> projectWithSpecification(final String projectName, final String ownerName, final Long ownerID) {
        return new Specification<Project>() {
            public Predicate toPredicate(Root<Project> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                ArrayList<Predicate> predicates = new ArrayList<Predicate>();

                if (!projectName.equals("")) {
                    predicates.add(builder.like(builder.upper(root.get("name").as(String.class)), "%" + projectName.toUpperCase() + "%"));
                }

                if (!ownerName.equals("")) {
                    // TODO Check for errors
                    predicates.add(builder.like(builder.upper(root.get("owner").get("name").as(String.class)), "%" + ownerName.toUpperCase() + "%"));
                }

                if (ownerID != null) {
                    // TODO Check for errors
                    predicates.add(builder.equal(root.get("owner").get("userId"), ownerID));
                }

                return builder.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
    }
}
