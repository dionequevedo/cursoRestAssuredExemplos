package br.adm.quevedo.rest;

import org.junit.Test;
import io.restassured.http.ContentType;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.hasXPath;
import static org.hamcrest.Matchers.*;

public class HTML {

    @Test
    public void deveFazerBuscarComHTML() {
        given()
                .log().all()
        .when()
                .get("https://restapi.wcaquino.me/v2/users")
        .then()
                .log().all()
                .statusCode(200)
                .contentType(ContentType.HTML)
                .body("html.body.div.table.tbody.tr.size()", is(3))
                .body("html.body.div.table.tbody.tr[1].td[2]", is("25"))
                .appendRootPath("html.body.div.table.tbody")
                .body("tr.find{it.toString().startsWith('2')}.td[1]", is("Maria Joaquina"))
        ;
    }

    @Test
    public void deveFazerBuscarComXpathEmHTML() {
        given()
                .log().all()
        .when()
                .get("https://restapi.wcaquino.me/v2/users?format=clean")
        .then()
                .log().all()
                .statusCode(200)
                .contentType(ContentType.HTML)
                .body(hasXPath("count(/html/body/table/tr)", is("4")))
//                .body("html.body.div.table.tbody.tr[1].td[2]", is("25"))
//                .appendRootPath("html.body.div.table.tbody")
                .body(hasXPath("/html/body/table/tr[3]/td[2]", is("Maria Joaquina")))
        ;
    }
}
