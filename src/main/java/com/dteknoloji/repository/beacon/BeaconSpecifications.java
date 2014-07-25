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
    public static Specification<Beacon> beaconWithSpecification(final String uuid, final String major, final String minor) {
        return new Specification<Beacon>() {
            public Predicate toPredicate(Root<Beacon> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                ArrayList<Predicate> predicates = new ArrayList<Predicate>();

                if (!uuid.equals("")) {
                    predicates.add(builder.equal(root.get("uuid"), uuid.toLowerCase()));
                }

                if (!major.equals("")) {
                    predicates.add(builder.equal(root.get("major"), major.toLowerCase()));
                }

                if (!minor.equals("")) {
                    predicates.add(builder.equal(root.get("minor"), minor.toLowerCase()));
                }

                return builder.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
    }
}
