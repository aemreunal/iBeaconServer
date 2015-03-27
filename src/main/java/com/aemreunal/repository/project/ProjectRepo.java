package com.aemreunal.repository.project;

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

import java.util.List;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.aemreunal.domain.Project;
import com.aemreunal.domain.User;

@Repository
public interface ProjectRepo extends CrudRepository<Project, Long>, JpaSpecificationExecutor {
    List<Project> findByOwner(User owner);

    Project findByOwnerAndProjectId(User owner, Long projectId);
}
