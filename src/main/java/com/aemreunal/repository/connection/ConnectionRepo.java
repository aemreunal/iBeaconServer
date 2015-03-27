package com.aemreunal.repository.connection;

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

import java.util.Set;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import com.aemreunal.domain.Connection;
import com.aemreunal.domain.Project;

@Repository
public interface ConnectionRepo extends PagingAndSortingRepository<Connection, Long>, JpaSpecificationExecutor {
    Set<Connection> findByProject(Project project);

    Connection findByConnectionIdAndProject(Long connectionId, Project project);
}
