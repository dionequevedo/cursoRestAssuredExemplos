package br.adm.quevedo.rest;

import io.restassured.http.Method;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static io.restassured.RestAssured.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertEquals;

public class UserJsonTest {
    @Test
    public void deveVerificarPrimeiroNivel() {
        given()
        .when()
                .get("https://restapi.wcaquino.me/users/1")
        .then()
                .statusCode(200)
                .body("id", is(1))
                .body("name", containsString("Silva"))
                .body("age", greaterThan(18))
        ;
    }

    @Test
    public void deveVerificarFaixaEtariaUsuarios() {
        given()
        .when()
                .get("https://restapi.wcaquino.me/users")
        .then()
                .statusCode(200)
                .body("age.findAll{it <= 25}.size()", is(2))
                .body("age.findAll{it <= 25 && it > 20}.size()", is(1))
                .body("findAll{it.age <= 25 && it.age > 20}.name", hasItem("Maria Joaquina"))
                .body("find{it.age <= 25 && it.age > 20}.name", is("Maria Joaquina"))
                .body("findAll{it.age <= 25}[0].name", is("Maria Joaquina"))
                .body("findAll{it.age <= 25}[1].name", is("Ana Júlia"))
                .body("findAll{it.name.contains('n')}.name", hasItems(is("Ana Júlia"), is("Maria Joaquina")))
                .body("findAll{it.name.length()>10}.name",hasItems(is("João da Silva"), is("Maria Joaquina")))
                .body("name.collect{it.toUpperCase()}", hasItem("MARIA JOAQUINA"))
                .body("name.findAll{it.startsWith('M')}.collect{it.toUpperCase()}", hasItem("MARIA JOAQUINA"))
                .body("name.findAll{it.startsWith('M')}.collect{it.toUpperCase()}.toArray()", allOf(arrayContaining("MARIA JOAQUINA"), arrayWithSize(1)))
                .body("age.collect{it * 2}", hasItems(60, 50, 40))
                .body("id.max()", is(3))
                .body("salary.min()", is(1234.5678f))
                .body("salary.findAll{it != null}.sum()", is(closeTo(3734.5677f, 0.001)))
                .body("salary.findAll{it != null}.sum()", allOf(greaterThan(3730.0d), lessThan(3740.0d)))
        ;
    }

    @Test
    public void deveVerificarSegundoNivel() {
        given()
                .when()
                .get("https://restapi.wcaquino.me/users/2")
                .then()
                .statusCode(200)
                .body("id", is(2))
                .body("name", containsString("Joaquina"))
                .body("endereco.rua", is("Rua dos bobos"))
        ;
    }

    @Test
    public void deveVerificarLista() {
        given()
        .when()
                .get("https://restapi.wcaquino.me/users/3")
        .then()
                .statusCode(200)
                .body("id", is(3))
                .body("name", containsString("Ana"))
                .body("filhos", hasSize(2))
                .body("filhos[0].name", is("Zezinho"))
                .body("filhos.name", hasItem("Luizinho"))
        ;
    }

    @Test
    public void deveVerificarListaNaRaiz() {
        given()
                .when()
                .get("https://restapi.wcaquino.me/users")
                .then()
                .statusCode(200)
                .body("name", hasSize(3))
                .body("name", hasItems(is("João da Silva"), is("Maria Joaquina"), is("Ana Júlia")))
                .body("age[1]", is(25))
                .body("filhos.name", hasItem(Arrays.asList("Zezinho", "Luizinho")))
                .body("salary", hasItem(2500))
        ;
    }

    @Test
    public void deveRetornarErro() {
        given()
        .when()
                .get("https://restapi.wcaquino.me/users/4")
        .then()
                .statusCode(404)
                .body("error", is("Usuário inexistente"))
        ;
    }

    @Test
    public void verificarJsonExtraindoResponsePrimeiroNivel() {
        Response response = request(Method.GET, "https://restapi.wcaquino.me/users/1");
        //path
        assertEquals(new Integer(1), response.path("id"));
        assertEquals(new Integer(1), response.path("%s","id"));
        assertEquals("João da Silva", response.path("name"));

        //Json Path
        JsonPath jpath = new JsonPath(response.asString());
        assertEquals(1, jpath.getInt("id"));

        //From
        int id = JsonPath.from(response.asString()).getInt("id");
        assertEquals(1, id);
    }

    @Test
    public void verificarJsonExtraindoResponseSegundoNivel() {
        Response response = request(Method.GET, "https://restapi.wcaquino.me/users/1");
        //path
        assertEquals(new Integer(1), response.path("id"));
        assertEquals(new Integer(1), response.path("%s","id"));
        assertEquals("João da Silva", response.path("name"));

        //Json Path
        JsonPath jpath = new JsonPath(response.asString());
        assertEquals(1, jpath.getInt("id"));

        //From
        int id = JsonPath.from(response.asString()).getInt("id");
        assertEquals(1, id);
    }

    @Test
    public void devoUnirJsonPathComJava(){
        ArrayList<String> names =
            given()
            .when()
                    .get("https://restapi.wcaquino.me/users")
            .then()
                .statusCode(200)
                .extract().path("name.findAll{it.startsWith('Maria')}")
            ;
        Assert.assertEquals(1, names.size());
        Assert.assertTrue(names.get(0).equalsIgnoreCase("mArIa Joaquina"));
        Assert.assertEquals(names.get(0).toUpperCase(), "maria joaquina".toUpperCase());
    }
}
