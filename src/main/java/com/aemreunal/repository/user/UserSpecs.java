package com.aemreunal.repository.user;

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

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import com.aemreunal.domain.User;

public class UserSpecs {
    /**
     * Creates the User search specification by the given username.
     *
     * @param username
     *     The username of the User to find
     *
     * @return The specification of the user with the given username
     */
    public static Specification<User> usernameSpecification(final String username) {
        return new Specification<User>() {
            public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                return builder.equal(root.get("username").as(String.class), username);
            }
        };
    }
}
