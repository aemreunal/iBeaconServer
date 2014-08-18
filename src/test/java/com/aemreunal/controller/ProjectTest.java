package com.aemreunal.controller;

import org.junit.Test;
import com.aemreunal.domain.ProjectInfo;
import com.aemreunal.domain.UserInfo;
import com.aemreunal.helper.EntityCreator;

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
        UserInfo userInfo = EntityCreator.createRandomUser();
        ProjectInfo projectInfo = EntityCreator.createRandomProject(userInfo.username);
        System.out.println(projectInfo);
    }
}
