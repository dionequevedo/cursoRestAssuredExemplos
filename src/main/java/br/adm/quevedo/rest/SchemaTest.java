package br.adm.quevedo.rest;

import static io.restassured.RestAssured.given;
import io.restassured.matcher.RestAssuredMatchers;
import io.restassured.module.jsv.JsonSchemaValidator;
import org.xml.sax.SAXParseException;
import org.junit.Test;


public class SchemaTest {

    @Test
    public void deveValidarSchemaXML() {
        given()
                .log().all()
        .when()
                .get("https://restapi.wcaquino.me/usersXML")
        .then()
                .log().all()
                .statusCode(200)
                .body(RestAssuredMatchers.matchesXsdInClasspath("users.xsd"))
        ;
    }

    @Test (expected = SAXParseException.class)
    public void naoDeveValidarSchemaXMLInvalido() {
        given()
                .log().all()
        .when()
                .get("https://restapi.wcaquino.me/invalidusersXML")
        .then()
                .log().all()
                .statusCode(200)
                .body(RestAssuredMatchers.matchesXsdInClasspath("users.xsd"))
        ;
    }

    @Test
    public void deveValidarSchemaJson() {
        given()
                .log().all()
        .when()
                .get("https://restapi.wcaquino.me/users")
        .then()
                .log().all()
                .statusCode(200)
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("users.json"))
        ;
    }

}