package com.aemreunal.repository.region;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import com.aemreunal.domain.Region;
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
public interface RegionRepo extends CrudRepository<Region, Long>, JpaSpecificationExecutor {
    Region findByRegionIdAndProject(Long regionId, Project project);
}
