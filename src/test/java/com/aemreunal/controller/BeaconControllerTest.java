package com.aemreunal.controller;

import java.util.List;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import com.aemreunal.config.controller.BeaconControllerTestConfig;
import com.aemreunal.config.controller.ProjectControllerTestConfig;
import com.aemreunal.domain.Beacon;
import com.aemreunal.domain.Project;
import com.aemreunal.handler.PrintHandler;
import com.aemreunal.service.BeaconService;
import com.aemreunal.service.ProjectService;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

/*
 * From: http://blog.zenika.com/index.php?post/2013/01/15/REST-Web-Services-testing-with-Spring-MVC
 *
 * TODO Add validation -> http://www.petrikainulainen.net/programming/spring-framework/spring-from-the-trenches-adding-validation-to-a-rest-api/
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { BeaconControllerTestConfig.class })
@WebAppConfiguration
//@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BeaconControllerTest {
    @Autowired
    private WebApplicationContext webAppContext;

    @Autowired
    private BeaconService beaconService;

    @Autowired
    private ProjectService projectService;

    private MockMvc realMvc;

    private static final Long PROJECT_ID = (long) 1;

    @Before
    public void setUp() {
        this.realMvc = MockMvcBuilders.webAppContextSetup(webAppContext).build();
    }

    @Test(timeout = 2000)
    public void getProjects() throws Exception {
        List<Project> projects = projectService.findAll();

        realMvc.perform(get("/Project").accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(content().contentType(ProjectControllerTestConfig.APPLICATION_JSON_UTF8))
               .andExpect(jsonPath("$", hasSize(projects.size())))
               .andExpect(jsonPath("$[0].projectId", Matchers.hasToString(projects.get(0).getProjectId().toString())))
               .andDo(new PrintHandler())
        ;
    }

    @Test(timeout = 2000)
    @Transactional
    public void checkBeaconsInProject1() throws Exception {
        Project project = projectService.findById(PROJECT_ID);
        Beacon[] beacons = project.getBeacons().toArray(new Beacon[1]);

        realMvc.perform(get("/Project/{projectId}/Beacon", PROJECT_ID).accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(content().contentType("application/json;charset=UTF-8"))
               .andExpect(jsonPath("$", hasSize(beacons.length)))
        ;

        for (int i = 0; i < beacons.length; i++) {
            realMvc.perform(get("/Project/{projectId}/Beacon", PROJECT_ID).accept(MediaType.APPLICATION_JSON))
                   .andExpect(status().isOk())
                   .andExpect(content().contentType("application/json;charset=UTF-8"))
                   .andExpect(jsonPath("$", hasSize(beacons.length)))
                   .andExpect(jsonPath("$[" + i + "].beaconId", hasToString(beacons[i].getBeaconId().toString())))
                   .andExpect(jsonPath("$[" + i + "].uuid", is(beacons[i].getUuid())))
                   .andExpect(jsonPath("$[" + i + "].major", is(beacons[i].getMajor())))
                   .andExpect(jsonPath("$[" + i + "].minor", is(beacons[i].getMinor())))
                   .andExpect(jsonPath("$[" + i + "].description", is(beacons[i].getDescription())))
                   .andExpect(jsonPath("$[" + i + "].creationDate", is(beacons[i].getCreationDate().getTime())))
            ;
        }
    }
}
