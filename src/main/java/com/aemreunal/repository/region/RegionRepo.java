package com.aemreunal.repository.region;

/*
 * *********************** *
 * Copyright (c) 2015      *
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
 * *********************** *
 */

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.aemreunal.domain.Project;
import com.aemreunal.domain.Region;

@Repository
public interface RegionRepo extends CrudRepository<Region, Long>, JpaSpecificationExecutor {
    Region findByRegionIdAndProject(Long regionId, Project project);
}
