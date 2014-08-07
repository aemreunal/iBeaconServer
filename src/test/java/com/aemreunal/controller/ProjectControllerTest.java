package com.aemreunal.controller;

import java.util.Arrays;
import java.util.Date;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import com.aemreunal.config.controller.ProjectControllerTestConfig;
import com.aemreunal.domain.Project;
import com.aemreunal.domain.ProjectBuilder;
import com.aemreunal.handler.PrintHandler;
import com.aemreunal.service.ProjectService;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.*;
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
@ContextConfiguration(classes = { ProjectControllerTestConfig.class })
@WebAppConfiguration
//@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ProjectControllerTest {
    @Autowired
    private WebApplicationContext webAppContext;

    @Autowired
    private ProjectService projectService;

    @Autowired
    @Mock
    private ProjectService projectServiceMock;

    @InjectMocks
    private ProjectController projectControllerWithMock;

    private MockMvc mockMvc;
    private MockMvc realMvc;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Mockito.reset(projectServiceMock);
        this.realMvc = MockMvcBuilders.webAppContextSetup(webAppContext).build();
        this.mockMvc = MockMvcBuilders.standaloneSetup(projectControllerWithMock).build();
    }

    @Test
    public void getProjects() throws Exception {
        mockMvc.perform(get("/Project").accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(content().contentType(ProjectControllerTestConfig.APPLICATION_JSON_UTF8))
               .andDo(new PrintHandler());
    }

    @Test
    public void getProjectWithId1() throws Exception {
        Long projectId = (long) 1;

        Project project = projectService.findById(projectId);

        realMvc.perform(get("/Project/{projectId}", projectId).accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(content().contentType("application/json;charset=UTF-8"))
               .andExpect(jsonPath("$.name", is(project.getName())));
    }

    /*
        @Test
        public void getMockProjectWithId1() throws Exception {
            Long projectId = (long) 1;
            Date date = new Date();

            Project mockProject = new ProjectBuilder().withProjectId(projectId)
                                                      .withName("Mock test project")
                                                      .withDescription("Lorem ipsum")
                                                      .withCreationDate(date)
                                                      .build();

            Mockito.when(projectServiceMock.findById(projectId)).thenReturn(mockProject);

            Project project = projectServiceMock.findById(projectId);

            mockMvc.perform(get("/Project/{projectId}", projectId).accept(MediaType.APPLICATION_JSON))
                   .andExpect(status().isOk())
                   .andExpect(content().contentType(ProjectControllerTestConfig.APPLICATION_JSON_UTF8))
                   .andExpect(jsonPath("$.name", is(project.getName())));

    //        assertEquals(projectServiceMock.findById(projectId).getName(), MockRestRequestMatchers.jsonPath("name").value(projectServiceMock.findById(projectId).getName()));
        }
        */

    @Test
    public void getMockProjects() throws Exception {
        Long project1Id = (long) 1;
        String name1 = "Mock test project 1";
        String desc1 = "Lorem ipsum";
        Date date1 = new Date();

        Project mockProject1 = new ProjectBuilder().withProjectId(project1Id)
                                                   .withName(name1)
                                                   .withDescription(desc1)
                                                   .withCreationDate(date1)
                                                   .build();

        Long project2Id = (long) 2;
        String name2 = "Mock test project 2";
        String desc2 = "Lorem ipsum dolor sit amet.";
        Date date2 = new Date();

        Project mockProject2 = new ProjectBuilder().withProjectId(project2Id)
                                                   .withName(name2)
                                                   .withDescription(desc2)
                                                   .withCreationDate(date2)
                                                   .build();

        Mockito.when(projectServiceMock.findAll()).thenReturn(Arrays.asList(mockProject1, mockProject2));

        mockMvc.perform(get("/Project").accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(content().contentType(ProjectControllerTestConfig.APPLICATION_JSON_UTF8))
               .andDo(new PrintHandler())
               .andExpect(jsonPath("$", hasSize(2)))
               .andExpect(jsonPath("$[0].projectId", Matchers.hasToString(String.valueOf(project1Id))))
               .andExpect(jsonPath("$[1].projectId", Matchers.hasToString(String.valueOf(project2Id))))
               .andExpect(jsonPath("$[0].name", is(name1)))
               .andExpect(jsonPath("$[1].name", is(name2)))
               .andExpect(jsonPath("$[0].description", is(desc1)))
               .andExpect(jsonPath("$[1].description", is(desc2)))
               .andExpect(jsonPath("$[0].creationDate", is(date1.getTime())))
               .andExpect(jsonPath("$[1].creationDate", is(date2.getTime())))
        ;

        verify(projectServiceMock, times(1)).findAll();
        verifyNoMoreInteractions(projectServiceMock);
    }
}
