package com.aemreunal.controller.scenario;

/*
 ***************************
 * Copyright (c) 2014      *
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

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import com.aemreunal.config.GlobalSettings;
import com.aemreunal.controller.project.ProjectController;
import com.aemreunal.controller.user.UserController;
import com.aemreunal.domain.Scenario;
import com.aemreunal.service.ScenarioService;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Controller
@RequestMapping(GlobalSettings.SCENARIO_PATH_MAPPING)
public class ScenarioController {
    @Autowired
    private ScenarioService scenarioService;

    // TODO add search by attributes
    @RequestMapping(method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public ResponseEntity<List<Scenario>> getAllScenariosOfUser(@PathVariable String username,
                                                                @PathVariable Long projectId) {
        List<Scenario> scenarios = scenarioService.getScenariosOfProject(username, projectId);
        return new ResponseEntity<List<Scenario>>(scenarios, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = GlobalSettings.SCENARIO_ID_MAPPING, produces = "application/json;charset=UTF-8")
    public ResponseEntity<Scenario> getScenario(@PathVariable String username,
                                                @PathVariable Long projectId,
                                                @PathVariable Long scenarioId) {
        Scenario scenario = scenarioService.getScenario(username, projectId, scenarioId);
        addLinks(username, projectId, scenarioId, scenario);
        return new ResponseEntity<Scenario>(scenario, HttpStatus.OK);
    }

    private void addLinks(String username, Long projectId, Long scenarioId, Scenario scenario) {
        scenario.add(ControllerLinkBuilder.linkTo(methodOn(ScenarioController.class).getScenario(username, projectId, scenarioId)).withSelfRel());
        scenario.add(ControllerLinkBuilder.linkTo(methodOn(UserController.class).getUserByUsername(username)).withRel("owner"));
        scenario.add(ControllerLinkBuilder.linkTo(methodOn(ProjectController.class).getProjectById(username, projectId)).withRel("project"));
    }

    @RequestMapping(method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public ResponseEntity<Scenario> createScenario(@PathVariable String username,
                                                     @PathVariable Long projectId,
                                                     @RequestBody Scenario scenarioFromJson,
                                                     UriComponentsBuilder builder) {
        Scenario savedScenario = scenarioService.save(username, projectId, scenarioFromJson);
        if (GlobalSettings.DEBUGGING) {
            System.out.println("Saved scenario with Name = \'" + savedScenario.getName() + "\' ID = \'" + savedScenario.getScenarioId() + "\'");
        }
        return buildCreateResponse(username, builder, savedScenario);
    }

    private ResponseEntity<Scenario> buildCreateResponse(String username, UriComponentsBuilder builder, Scenario savedScenario) {
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(builder.path(GlobalSettings.SCENARIO_SPECIFIC_MAPPING)
                                   .buildAndExpand(
                                       username,
                                       savedScenario.getProject().getProjectId(),
                                       savedScenario.getScenarioId()
                                   )
                                   .toUri());
        return new ResponseEntity<Scenario>(savedScenario, headers, HttpStatus.CREATED);
    }


                                                            @RequestMapping(method = RequestMethod.DELETE, value = GlobalSettings.SCENARIO_ID_MAPPING, produces = "application/json;charset=UTF-8")
    public ResponseEntity<Scenario> deleteScenario(@PathVariable String username,
                                                   @PathVariable Long projectId,
                                                   @PathVariable Long scenarioId,
                                                   @RequestParam(value = "confirm", required = true) String confirmation) {
        if (confirmation.toLowerCase().equals("yes")) {
            Scenario deletedScenario = scenarioService.delete(username, projectId, scenarioId);
            return new ResponseEntity<Scenario>(deletedScenario, HttpStatus.OK);
        } else {
            return new ResponseEntity<Scenario>(HttpStatus.PRECONDITION_FAILED);
        }
    }
}
