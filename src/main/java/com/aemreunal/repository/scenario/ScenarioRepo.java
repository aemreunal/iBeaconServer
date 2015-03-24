package com.aemreunal.repository.scenario;

/*
 ***************************
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
 ***************************
 */

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.aemreunal.domain.Project;
import com.aemreunal.domain.Scenario;

@Repository
public interface ScenarioRepo extends CrudRepository<Scenario, Long>, JpaSpecificationExecutor {
    public Scenario findByScenarioIdAndProject(Long scenarioId, Project project);
}
