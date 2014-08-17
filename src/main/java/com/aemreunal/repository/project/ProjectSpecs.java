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
import com.aemreunal.domain.User;

public class ProjectSpecs {

    /**
     * Creates the Project search specification from the given attributes
     *
     * @param owner
     *     The owner of the projects to search in
     * @param projectName
     *     The Project Name attribute to search for
     *
     * @return The specification of the project
     */
    public static Specification<Project> projectWithSpecification(final User owner, final String projectName) {
        return new Specification<Project>() {
            public Predicate toPredicate(Root<Project> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                ArrayList<Predicate> predicates = new ArrayList<Predicate>();

                predicates.add(builder.equal(root.get("owner").as(User.class), owner));

                if (!projectName.equals("")) {
                    predicates.add(builder.like(builder.upper(root.get("name").as(String.class)), "%" + projectName.toUpperCase() + "%"));
                }

                return builder.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
    }

    // TODO project date search: http://stackoverflow.com/a/5790681/2246876
}
