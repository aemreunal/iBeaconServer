package com.aemreunal.repository.beacon;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import com.aemreunal.domain.Beacon;
import com.aemreunal.domain.Project;

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
public interface BeaconRepository extends CrudRepository<Beacon, Long>, JpaSpecificationExecutor {
    Beacon findByBeaconIdAndProject(Long beaconId, Project project);
}
