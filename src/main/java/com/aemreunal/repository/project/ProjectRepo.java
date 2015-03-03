package com.aemreunal.repository.project;

import java.util.List;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.aemreunal.domain.Project;
import com.aemreunal.domain.User;

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

@Repository
public interface ProjectRepo extends CrudRepository<Project, Long>, JpaSpecificationExecutor {
    public List<Project> findByOwner(User owner);

    public Project findByOwnerAndProjectId(User owner, Long projectId);
}
