package com.aemreunal.repository.beacon;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.aemreunal.domain.Beacon;
import com.aemreunal.domain.Region;

/*
 **************************
 * Copyright (c) 2015     *
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
@Repository
public interface BeaconRepo extends CrudRepository<Beacon, Long>, JpaSpecificationExecutor {
    Beacon findByBeaconIdAndRegion(Long beaconId, Region region);
}
