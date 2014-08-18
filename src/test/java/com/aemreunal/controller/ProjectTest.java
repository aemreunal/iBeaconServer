package com.aemreunal.controller;

import org.junit.Test;
import com.aemreunal.domain.user.UserInfo;
import com.aemreunal.domain.project.ProjectCreator;
import com.aemreunal.domain.project.ProjectInfo;
import com.aemreunal.domain.user.UserCreator;

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
 * https://code.google.com/p/rest-assured/
 *
 * Documentation: http://code.google.com/p/rest-assured/wiki/Usage
 */

public class ProjectTest {
    @Test
    public void testCreateProject() {
        UserInfo userInfo = UserCreator.createRandomUser();
        ProjectInfo projectInfo = ProjectCreator.createRandomProject(userInfo.username);
        System.out.println(projectInfo);
    }
}
