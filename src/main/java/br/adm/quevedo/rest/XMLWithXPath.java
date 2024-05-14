package br.adm.quevedo.rest;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Assert;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.hasItems;

public class XMLWithXPath {

    public static RequestSpecification reqSpec;
    public static ResponseSpecification resSpec;
    @BeforeClass        /*  Configura o SetUp que será válido para todos os testes  */
    public static void setUp(){

//        RestAssured.baseURI = "https://restapi.wcaquino.me";
//        RestAssured.port = 443;
//        RestAssured.basePath = "/v2";

        /*  Setando definições de request usando o RequestSpecBuilder */
        RequestSpecBuilder reqBuilder = new RequestSpecBuilder();
        reqBuilder.setBaseUri("https://restapi.wcaquino.me");
        reqBuilder.setPort(443);
        reqBuilder.setContentType("application/xml");
        reqBuilder.log(LogDetail.ALL);
        reqSpec = reqBuilder.build();

        /*  Setando definições de response usando o ResponseSpecBuilder */
        ResponseSpecBuilder resBuilder = new ResponseSpecBuilder();
        resBuilder.expectStatusCode(200);
        resSpec = resBuilder.build();

        /*  Forçando a execução dos requests e responses padrões em todos os testes */

        RestAssured.requestSpecification = reqSpec;
        RestAssured.responseSpecification = resSpec;
    }


    @Test
    public void devoFazerPesquisasAvancadasComXPath() {

        given()
        .when()
                .get("/usersXML")
        .then()
                .body(hasXPath("count(/users/user)", is("3")))
                .body(hasXPath("(/users/user[@id = '1'])"))
                .body(hasXPath("(//user[@id = '3'])"))
                .body(hasXPath("(//filhos/name[text() = 'Zezinho']/../../name)", is("Ana Julia")))
                .body(hasXPath("(//name[text() = 'Ana Julia']/following-sibling::filhos)", allOf(containsString("Zezinho"), containsString("Luizinho"))))
                .body(hasXPath("//name", is("João da Silva")))
                .body(hasXPath("/users/user[2]/name", is("Maria Joaquina")))
                .body(hasXPath("/users/user[last()]/name", is("Ana Julia")))
                .body(hasXPath("count(/users/user/name[contains(., 'n')])", is("2")))
                .body(hasXPath("//user[age < 24]/name", is("Ana Julia")))
                .body(hasXPath("//user[age > 20 and age < 30]/name", is("Maria Joaquina")))
        ;
    }
}
