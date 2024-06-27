package org.shreeram.v1.child;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.smallrye.reactive.messaging.memory.InMemoryConnector;
import io.smallrye.reactive.messaging.memory.InMemorySink;
import io.smallrye.reactive.messaging.memory.InMemorySource;
import jakarta.enterprise.inject.Any;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.MediaType;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.*;
import org.shreeram.v1.child.domain.Person;
import org.shreeram.v1.child.messaging.KafkaInMemoryBrokerLifeCycleManager;
import org.shreeram.v1.child.resource.PersonResource;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@QuarkusTestResource(KafkaInMemoryBrokerLifeCycleManager.class)
@TestHTTPEndpoint(PersonResource.class)
class PersonResourceTest {
    private Person person;
    @Inject
    @Any
    private InMemoryConnector connector;

    @BeforeEach
    void setUp() {
        person = new Person();
        person.setId(1L);
        person.setName("Raghav");
        person.setAge(32);
    }

    @AfterEach
    void tearDown() {
        person = null;
    }

    @Test
    void sample() {
        System.out.println(String.format("/persons?name=%s&age=%d", person.getName(), person.getAge()));
    }

    @Test
    void givenUrl_whenPersonData_thenReturn200() {
        RestAssured
                .given()
                .accept(MediaType.APPLICATION_JSON)
                .when()
                .get("/persons")
                .then()
                .contentType(MediaType.APPLICATION_JSON)
                .statusCode(HttpStatus.SC_OK);
    }

    @Test
    void givenUrl_whenPersons_thenReturn200() {
        RestAssured
                .when()
                .get("/persons")
                .then()
                .log().body()
                .log().headers()
                .contentType(MediaType.APPLICATION_JSON)
                .body("$", isA(List.class)) // Assert the response is a list
                .body("$", hasSize(2)) // Assert list size of 2
                .statusCode(HttpStatus.SC_OK);

    }

    @Test
    void givenValidId_whenPerson_thenReturn200() {
        RestAssured
                .given()
                .accept(ContentType.JSON)
                .when()
                .get("/persons/1")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("id", equalTo(person.getId()))
                .body("name", equalTo(person.getName()))
                .body("age", equalTo(person.getAge()));
    }

    @Test
    @Order(1)
    void givenPerson_whenConsumed_thenPushedToCorrectTopic() {
        InMemorySource<Person> eventIn = connector.source("persons-in");
        InMemorySink<Person> eventOut = connector.sink("persons-out2");
        eventIn.send(person);
        assertEquals(person, eventOut.received().get(0).getPayload());
    }

    @Test
    @Order(1)
    void givenPersonData_whenCreate_thenReturn201() {
        Person newPerson = new Person();
        newPerson.setId(3L);
        newPerson.setName("newPerson");
        newPerson.setAge(33);
        RestAssured
                .given()
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(newPerson)
                .when()
                .post("/persons")
                .then()
                .statusCode(HttpStatus.SC_CREATED)
                .log().body();
        RestAssured
                .given()
                .accept(MediaType.APPLICATION_JSON)
                .when()
                .get(String.format("/persons?name=%s&age=%d", newPerson.getName(), newPerson.getAge()))
                .then()
                .statusCode(HttpStatus.SC_OK);
    }

    @Test
    @Order(2)
    void givenPersonIdandData_whenUpdate_thenReturn200() {
        Person found = RestAssured
                .given()
                .accept(MediaType.APPLICATION_JSON)
                .when()
                .get("/persons/3")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("$", notNullValue())
                .body("$", hasItems("id", "name", "age"))
                .extract().as(Person.class);
        found.setName("updatedName");
        RestAssured
                .given()
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(found)
                .when()
                .put(String.format("/persons/%d", found.getId()))
                .then()
                .statusCode(HttpStatus.SC_OK)
                .log().body();
        RestAssured
                .given()
                .accept(MediaType.APPLICATION_JSON)
                .when()
                .get(String.format("/persons/%d", found.getId()))
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("name", equalTo(found.getName()));
    }

    @Test
    @Order(3)
    void givenPersonId_whenDelete_thenReturn200() {
        int id = 3;
        RestAssured
                .given()
                .when()
                .delete(String.format("/persons/%d", id))
                .then()
                .statusCode(HttpStatus.SC_OK)
                .log().body();
        RestAssured
                .given()
                .accept(MediaType.APPLICATION_JSON)
                .when()
                .get(String.format("/persons/%d", id))
                .then()
                .statusCode(HttpStatus.SC_NOT_FOUND);
    }

}
