package com.aemreunal.controller;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.json.*;
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
import org.springframework.web.context.WebApplicationContext;
import com.aemreunal.config.controller.BeaconControllerTestConfig;
import com.aemreunal.config.controller.ProjectControllerTestConfig;
import com.aemreunal.domain.Beacon;
import com.aemreunal.domain.BeaconBuilder;
import com.aemreunal.domain.Project;
import com.aemreunal.handler.PrintHandler;
import com.aemreunal.service.BeaconService;
import com.aemreunal.service.ProjectService;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
//@TransactionConfiguration(defaultRollback = false)
//@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BeaconControllerTest {
    @Autowired
    private WebApplicationContext webAppContext;

    @Autowired
    private BeaconService beaconService;

    @Autowired
    private ProjectService projectService;

    private static final Long PROJECT_ID = (long) 1;

    private Random random = new Random();
    private MockMvc realMvc;

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
               .andExpect(jsonPath("$[0].projectId", hasToString(projects.get(0).getProjectId().toString())))
               .andDo(new PrintHandler())
        ;
    }
/*

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
*/

    @Test
//    @Transactional
//    @Rollback
    public void checkGeneratedBeaconsInProject1() throws Exception {
        Project project = projectService.findById(PROJECT_ID);
        Set<Beacon> beaconSet = project.getBeacons();
        beaconSet.size();

        // Delete all existing beacons
        Beacon[] beaconArray = beaconSet.toArray(new Beacon[beaconSet.size()]);
        System.out.println(beaconArray.length);
        for (Beacon beacon : beaconArray) {
            realMvc.perform(delete("/Project/{projectId}/Beacon/{beaconId}?confirm=yes", PROJECT_ID, beacon.getBeaconId()))
                   .andExpect(status().isOk())
            ;
        }

        realMvc.perform(get("/Project/{projectId}/Beacon", PROJECT_ID))
               .andExpect(jsonPath("$", hasSize(0)));

        // Insert generated beacons
        ArrayList<Beacon> beacons = generateTestBeacons();

        // Check if beacons are inserted
        for (int i = 0; i < beacons.size(); i++) {
            realMvc.perform(get("/Project/{projectId}/Beacon", PROJECT_ID).accept(MediaType.APPLICATION_JSON))
                   .andExpect(status().isOk())
                   .andExpect(content().contentType("application/json;charset=UTF-8"))
                   .andExpect(jsonPath("$", hasSize(beacons.size())))
                   .andExpect(jsonPath("$[" + i + "].beaconId", hasToString(beacons.get(i).getBeaconId().toString())))
                   .andExpect(jsonPath("$[" + i + "].uuid", is(beacons.get(i).getUuid())))
                   .andExpect(jsonPath("$[" + i + "].major", is(beacons.get(i).getMajor())))
                   .andExpect(jsonPath("$[" + i + "].minor", is(beacons.get(i).getMinor())))
                   .andExpect(jsonPath("$[" + i + "].description", is(beacons.get(i).getDescription())))
                   .andExpect(jsonPath("$[" + i + "].creationDate", is(beacons.get(i).getCreationDate().getTime())))
            ;
        }
    }

    private ArrayList<Beacon> generateTestBeacons() {
        ArrayList<JsonObject> beaconsJson = readRandomBeacons();
        Project project = projectService.findById(PROJECT_ID);
        ArrayList<Beacon> beacons = convertBeaconJson(project, beaconsJson);
//        insertBeacons(beacons);
        insertBeacons(beaconsJson);
        return beacons;
    }

    private ArrayList<JsonObject> readRandomBeacons() {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream("data/test_beacons.txt");
            return getBeaconJson(inputStream);
        } catch (FileNotFoundException e) {
            System.err.println("\"data/test_beacons.txt\" file not found!");
            System.exit(-1);
        }
        return null;
    }

    private ArrayList<JsonObject> getBeaconJson(InputStream inputStream) {
        ArrayList<JsonObject> beaconsJson = new ArrayList<>();

        JsonReader reader = Json.createReader(inputStream);
        JsonArray beaconsJsonArray = reader.readArray();
        for (JsonValue jsonValue : beaconsJsonArray) {
            beaconsJson.add((JsonObject) jsonValue);
        }

        return beaconsJson;
    }

    private ArrayList<Beacon> convertBeaconJson(Project project, ArrayList<JsonObject> beaconsJson) {
        ArrayList<Beacon> beacons = new ArrayList<>();
        for (JsonObject beaconJson : beaconsJson) {
            beacons.add(new BeaconBuilder().withJson(beaconJson).withProject(project).build());
        }
        return beacons;
    }

    private void insertBeacons(ArrayList<JsonObject> beacons) {
        for (JsonObject beaconJson : beacons) {
            try {
                realMvc.perform(post("/Project/{projectId}/Beacon", PROJECT_ID).contentType(MediaType.APPLICATION_JSON).content(beaconJson.toString()))
                       .andExpect(status().is(201))
                ;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
