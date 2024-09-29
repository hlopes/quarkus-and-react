package com.example.fullstack.user;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.jboss.resteasy.reactive.RestResponse;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

@QuarkusTest
class UserResourceTest {

    private final String API_URL = "/api/v1/users";

    @Test
    @TestSecurity(user = "admin", roles = "admin")
    void list() {
        RestAssured
            .given()
            .when()
            .get(API_URL)
            .then()
            .statusCode(RestResponse.StatusCode.OK)
            .body("$.size()", greaterThanOrEqualTo(1), "[0].name", is("admin"), "[0].password",
                  nullValue());
    }

    @Test
    @TestSecurity(user = "admin", roles = "admin")
    void create() {
        RestAssured
            .given()
            .body("{\"name\":\"test\", \"password\":\"test\", \"roles\":[\"user\"]}")
            .contentType(ContentType.JSON)
            .when()
            .post(API_URL)
            .then()
            .statusCode(RestResponse.StatusCode.CREATED)
            .body("name", is("test"), "password", nullValue(), "created", not(emptyString()));
    }

    @Test
    @TestSecurity(user = "user", roles = "user")
    void createForbidden() {
        RestAssured
            .given()
            .body("{\"name\":\"test\", \"password\":\"test\", \"roles\":[\"user\"]}")
            .contentType(ContentType.JSON)
            .when()
            .post(API_URL)
            .then()
            .statusCode(RestResponse.StatusCode.FORBIDDEN)
            .body(emptyString());
    }

    @Test
    @TestSecurity(user = "admin", roles = "admin")
    void createDuplicated() {
        RestAssured
            .given()
            .body("{\"name\":\"user\", \"password\":\"test\", \"roles\":[\"user\"]}")
            .contentType(ContentType.JSON)
            .when()
            .post(API_URL)
            .then()
            .statusCode(RestResponse.StatusCode.CONFLICT)
            .body(emptyString());
    }

    @Test
    @TestSecurity(user = "admin", roles = "admin")
    void update() {
        var user = RestAssured
            .given()
            .body("{\"name\":\"to-update\", \"password\":\"test\", \"roles\":[\"user\"]}")
            .contentType(ContentType.JSON)
            .when()
            .post(API_URL)
            .as(User.class);

        user.name = "updated";

        RestAssured
            .given()
            .body(user)
            .contentType(ContentType.JSON)
            .when()
            .put(API_URL + "/" + user.id)
            .then()
            .statusCode(RestResponse.StatusCode.OK)
            .body("name", is("updated"));
    }
}
