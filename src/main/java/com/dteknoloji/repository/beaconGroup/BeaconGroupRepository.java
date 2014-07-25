package com.dteknoloji.repository.beaconGroup;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import com.dteknoloji.domain.BeaconGroup;

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

// extends CrudRepository<Object type, Object ID type>
public interface BeaconGroupRepository extends CrudRepository<BeaconGroup, Long>, JpaSpecificationExecutor {

}
