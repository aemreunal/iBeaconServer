package com.aemreunal.controller;

import org.junit.Test;
import com.aemreunal.domain.ProjectInfo;
import com.aemreunal.domain.UserInfo;
import com.aemreunal.helper.EntityCreator;
import com.jayway.restassured.specification.ResponseSpecification;

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

public class ProjectControllerRestAssuredTest {

    private ResponseSpecification responseSpec;
/*

    @Before
    public void createResponseSpec() {
        ResponseSpecBuilder builder = new ResponseSpecBuilder();
        builder.expectStatusCode(201);
        builder.expectContentType(ContentType.JSON);
        this.responseSpec = builder.build();

        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails(LogDetail.ALL);
    }
*/

    @Test
    public void testCreateUser() {
        UserInfo userInfo = EntityCreator.createRandomUser();
        System.out.println(userInfo);
    }

    @Test
    public void testCreateProject() {
        UserInfo userInfo = EntityCreator.createRandomUser();
        ProjectInfo projectInfo = EntityCreator.createRandomProject(userInfo.username);
        System.out.println(projectInfo);
    }
/*
    @Test
    public void testGetProjects() {
        JsonPath response = given()
            .contentType(ContentType.JSON)
            .log().ifValidationFails()

            .when()
            .get("/Project")

            .then()
            .log().ifValidationFails()
            .statusCode(HttpStatus.SC_CREATED)
            .contentType(ContentType.JSON)
//            .spec(this.responseSpec)
//            .body("$", arrayWithSize(4))

            .extract().body().jsonPath();

        assertTrue(response.getList("").size() == 4);
    }

    @Test
    public void testDumpDB() {
        // http://www.dbunit.org/howto.html
        DatabaseHelper dbHelper = new DatabaseHelper();
        assertTrue("Failure to dump database!", dbHelper.extractFromDB());
    }
    */
}
