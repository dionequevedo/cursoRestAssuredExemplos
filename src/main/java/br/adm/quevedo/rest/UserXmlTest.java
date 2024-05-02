package br.adm.quevedo.rest;

import groovy.util.NodeList;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

import static io.restassured.RestAssured.*;
import io.restassured.internal.path.xml.NodeBase;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.hasItems;

public class UserXmlTest {
    @Test
    public void testWithXMLPath(){
        given()
        .when()
                .get("https://restapi.wcaquino.me/usersXML/3")
        .then()
                .statusCode(200)
                .rootPath("user")                       // Define o Root Path do XML
                .body("name", is("Ana Julia"))
                .body("@id", is("3"))
                .rootPath("user.filhos")                // Define o Root Path do XML
                .body("name.size()", is(2))
                .body("name[0]", is("Zezinho"))
                .detachRootPath("filhos")               // Remove um subnível do Root Path do XML
                .body("filhos.name[1]", is("Luizinho"))
                .body("filhos.name", hasItem("Luizinho"))
                .appendRootPath("filhos")               // Adiciona um subnível ao Root Path do XML
                .body("filhos.name", hasItems(is("Luizinho"), is("Zezinho")))
        ;
    }

    @Test
    public void devoFazerPesquisasAvancadasComXML(){
        given()
                .when()
                .get("https://restapi.wcaquino.me/usersXML")
                .then()
                .statusCode(200)
                .rootPath("users.user")                       // Define o Root Path do XML
                .body("size()", is(3))
                .body("findAll{it.age.toInteger() <= 25}.size()", is(2))
                .body("@id", hasItems("1", "2", "3"))       // atributos devem ser identificados com um @
                .body(".findAll{it.age == 25}.name", is("Maria Joaquina"))
                .body(".findAll{it.name.toString().contains('n')}.name", hasItems("Maria Joaquina", "Ana Julia"))
                .body("salary.find{it != null}.toDouble()", is(1234.5678d))
                .body("age.collect{it.toInteger() * 2}", hasItems(40, 50, 60 ))
                .body("name.findAll{it.toString().startsWith('Maria')}.collect{it.toString().toUpperCase()}", is("MARIA JOAQUINA"))
        ;
    }

    @Test
    public void devoFazerPesquisasAvancadasComXML2(){
        Object path2 =
        given()
        .when()
                .get("https://restapi.wcaquino.me/usersXML")
        .then()
                .statusCode(200)
                .extract().path("users.user.name.findAll{it.toString().startsWith('Maria')}");
        Assert.assertEquals("Maria Joaquina".toUpperCase(), path2.toString().toUpperCase());
    }

    @Test
    public void devoFazerPesquisasAvancadasComXML3(){
        ArrayList<NodeBase> nomes =
                given()
                .when()
                        .get("https://restapi.wcaquino.me/usersXML")
                .then()
                        .statusCode(200)
                        .extract().path("users.user.name.findAll{it.toString().contains('n')}");
        Assert.assertEquals(2, nomes.size());
        Assert.assertEquals("Maria Joaquina".toUpperCase(), nomes.get(0).toString().toUpperCase());
        Assert.assertTrue("ANA JULIA".equalsIgnoreCase(nomes.get(1).toString()));
    }
}
