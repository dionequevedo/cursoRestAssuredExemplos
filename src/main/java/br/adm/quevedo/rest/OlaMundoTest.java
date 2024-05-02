package br.adm.quevedo.rest;

import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.response.Response;
import static io.restassured.RestAssured.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class OlaMundoTest {
    @Test
    public void testOlaMundo(){
        Response response = RestAssured.request(Method.GET, "http://restapi.wcaquino.me:80/ola");
        Assert.assertTrue(response.getBody().asString().equals("Ola Mundo!"));
    }

    @Test
    public void testOlaMundoComErro(){
        Response response = RestAssured.request(Method.GET, "http://restapi.wcaquino.me:80/ola");
        Assert.assertFalse(response.getBody().asString().equals("Ola Mundo"));
    }

    @Test
    public void testStatusCode200(){
        Response response = RestAssured.request(Method.GET, "http://restapi.wcaquino.me:80/ola");
        Assert.assertTrue("O status esperado deveria ser 200",response.statusCode() == 200);
    }

    @Test
    public void testStatusCode201ComErro(){
        Response response = RestAssured.request(Method.GET, "http://restapi.wcaquino.me:80/ola");
        Assert.assertEquals("O status esperado deve ser 200", 200, response.statusCode());
    }

    @Test
    public void testConhecerOutrasFormasRestAssured(){
        get("http://restapi.wcaquino.me:80/ola").then().statusCode(200);
    }

    @Test
    public void testComGerkin(){
        given()
        .when()
                .get("http://restapi.wcaquino.me:80/ola")
        .then()
                .assertThat()
                .statusCode(200);
    }

    @Test
    public void HamcrestMatchersIs(){
        Response response = RestAssured.request(Method.GET, "http://restapi.wcaquino.me:80/ola");
        assertThat(response.getBody().asString().toString(),Matchers.is("Ola Mundo!"));
    }

    @Test
    public void HamcrestHasSize(){
        List<Integer> impares = Arrays.asList(1, 3, 5, 7, 9);
        assertThat(impares, hasSize(5));
    }

    @Test
    public void HamcrestContais(){
        List<Integer> impares = Arrays.asList(1, 3, 5, 7, 9);
        assertThat(impares, contains(1, 3, 5, 7, 9));
    }

    @Test
    public void HamcrestContaisInAnyOrder(){
        List<Integer> impares = Arrays.asList(1, 3, 5, 7, 9);
        assertThat(impares, containsInAnyOrder(1, 3, 5, 9, 7));
    }

    @Test
    public void HamcrestHasItem(){
        List<Integer> impares = Arrays.asList(1, 3, 5, 7, 9);
        assertThat(impares, hasItem(5));
    }

    @Test
    public void HamcrestIsNot(){
        assertThat("Maria", is(not("Joao")));
    }

    @Test
    public void HamcrestIs(){
        assertThat("Maria", is("Maria"));
    }

    @Test
    public void HamcrestAnyOf(){
        assertThat("Maria", anyOf(is("Joana"), is("Marcela"), is("Maria"), is("Rita")));
    }

    @Test
    public void HamcrestAllOf(){
        assertThat("Joaquina", allOf(startsWith("Jo"), endsWith("ina"), containsString("qu")));
    }

    @Test
    public void ValidarOBody(){
        given()
        .when()
               .get("http://restapi.wcaquino.me:80/ola")
        .then()
               .statusCode(200)
               .body(containsString("Mundo"));
    }
}
