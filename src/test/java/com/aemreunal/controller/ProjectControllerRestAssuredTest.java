package com.aemreunal.controller;

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

import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import com.aemreunal.helper.DatabaseHelper;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.builder.ResponseSpecBuilder;
import com.jayway.restassured.filter.log.LogDetail;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.specification.ResponseSpecification;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.assertTrue;

public class ProjectControllerRestAssuredTest {

    private ResponseSpecification responseSpec;

    @Before
    public void createResponseSpec() {
        ResponseSpecBuilder builder = new ResponseSpecBuilder();
        builder.expectStatusCode(201);
        builder.expectContentType(ContentType.JSON);
        this.responseSpec = builder.build();

        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails(LogDetail.ALL);
    }

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
        DatabaseHelper dbHelper = new DatabaseHelper();
        assertTrue("Failure to dump database!", dbHelper.extractFromDB());
    }
}
