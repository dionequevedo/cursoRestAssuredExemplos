package br.adm.quevedo.rest;

import org.junit.Test;
import org.junit.Assert;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.hasItems;

public class XMLWithXPath {
    @Test
    public void devoFazerPesquisasAvancadasComXPath() {
        given()
        .when()
                .get("https://restapi.wcaquino.me/usersXML")
        .then()
                .statusCode(200)
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
