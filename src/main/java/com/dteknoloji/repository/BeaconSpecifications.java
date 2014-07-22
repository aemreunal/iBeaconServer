package com.dteknoloji.repository;

/*
 * This code belongs to:
 * Ahmet Emre Unal
 * S001974
 * emre.unal@ozu.edu.tr
 */

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
                // TODO MUST ENSURE ALL EMPTY PARAMETERS DON'T REACH HERE
                // TODO MUST ENSURE ALL EMPTY PARAMETERS DON'T REACH HERE
                // TODO MUST ENSURE ALL EMPTY PARAMETERS DON'T REACH HERE
                // TODO MUST ENSURE ALL EMPTY PARAMETERS DON'T REACH HERE
                Predicate predicate = null;

                if(!uuid.equals("")) {
                    if(predicate == null) {
                        predicate = builder.equal(root.get("uuid"), uuid.toLowerCase());
                    } else {
                        predicate = builder.and(builder.equal(root.get("uuid"), uuid.toLowerCase()), predicate);
                    }
                }

                if(!major.equals("")) {
                    if(predicate == null) {
                        predicate = builder.equal(root.get("major"), major);
                    } else {
                        predicate = builder.and(builder.equal(root.get("major"), major), predicate);
                    }
                }

                if(!minor.equals("")) {
                    if(predicate == null) {
                        predicate = builder.equal(root.get("minor"), minor);
                    } else {
                        predicate = builder.and(builder.equal(root.get("minor"), minor), predicate);
                    }
                }

                return predicate;
            }

//            private Predicate mergePredicate(CriteriaBuilder builder, Predicate base, Predicate toAdd) {
//                if (base == null) {
//                    return toAdd;
//                } else {
//                    return builder.and(base, toAdd);
//                }
//            }
        };
    }
}
