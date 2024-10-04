package com.example.fullstack.user;

import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.quarkus.vertx.VertxContextSupport;
import io.restassured.http.ContentType;
import org.jboss.resteasy.reactive.RestResponse;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
class UserResourceTest {

    private static final Logger log = LoggerFactory.getLogger(UserResourceTest.class);
    private final String API_URL = "/api/v1/users";

    @Test
    @TestSecurity(user = "admin", roles = "admin")
    void list() {

        given()
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

        given()
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

        given()
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
        given()
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
        var user = given()
            .body("{\"name\":\"to-update\", \"password\":\"test\", \"roles\":[\"user\"]}")
            .contentType(ContentType.JSON)
            .when()
            .post(API_URL)
            .as(User.class);

        user.name = "updated";

        given()
            .body(user)
            .contentType(ContentType.JSON)
            .when()
            .put(API_URL + "/" + user.id)
            .then()
            .statusCode(RestResponse.StatusCode.OK)
            .body("name", is("updated"));
    }

    @Test
    @TestSecurity(user = "admin", roles = "admin")
    void updateOptimisticLock() {
        given()
            .body("{\"name\":\"updated\", \"version\":1337}")
            .contentType(ContentType.JSON)
            .when()
            .put(API_URL + "/0")
            .then()
            .statusCode(RestResponse.StatusCode.CONFLICT);
    }

    @Test
    @TestSecurity(user = "admin", roles = "admin")
    void delete() throws Throwable {
        var toDelete = given()
            .body("{\"name\":\"to-delete\", \"password\":\"test\"}")
            .contentType(ContentType.JSON)
            .post(API_URL)
            .as(User.class);

        given()
            .when()
            .delete(API_URL + "/" + toDelete.id)
            .then()
            .statusCode(RestResponse.StatusCode.NO_CONTENT);

        assertThat(VertxContextSupport.subscribeAndAwait(
            () -> Panache.withSession(() -> User.findById(toDelete.id))), nullValue());
    }

    @Test
    @TestSecurity(user = "admin", roles = "user")
    void changePassword() throws Throwable {
        given()
            .body("{\"currentPassword\":\"quarkus\", \"newPassword\":\"changed\"}")
            .contentType(ContentType.JSON)
            .when()
            .put(API_URL + "/self/password")
            .then()
            .statusCode(RestResponse.StatusCode.OK);

        assertTrue(BcryptUtil.matches("changed", VertxContextSupport.subscribeAndAwait(
            () -> Panache.withSession(() -> User.<User>findById(0L))).password));
    }
}
