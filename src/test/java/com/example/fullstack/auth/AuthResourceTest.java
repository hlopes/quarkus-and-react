package com.example.fullstack.auth;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.jboss.resteasy.reactive.RestResponse;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.not;

@QuarkusTest
class AuthResourceTest {

    @Test
    void loginValidCredentials() {
        RestAssured
            .given()
            .body("{\"name\": \"admin\", \"password\":\"quarkus\"}")
            .contentType(ContentType.JSON)
            .when()
            .post("/api/v1/auth/login")
            .then()
            .statusCode(RestResponse.StatusCode.OK)
            .body(not(emptyString()));
    }

    @Test
    void loginInvalidCredentials() {
        RestAssured
            .given()
            .body("{\"name\": \"admin\", \"password\":\"not-quarkus\"}")
            .contentType(ContentType.JSON)
            .when()
            .post("/api/v1/auth/login")
            .then()
            .statusCode(RestResponse.StatusCode.UNAUTHORIZED)
            .body(emptyString());
    }
}
