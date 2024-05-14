package br.adm.quevedo.rest;

import org.junit.Assert;
import org.junit.Test;
import io.restassured.RestAssured;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class VerbosTest {

    @Test
    public void deveSalvarUsuarioJSon() {
        given()
                .log().all()
                .contentType("application/json")
                .body("{\"name\":\"Dione\",\"age\":\"45\",\"salary\":\"5960.45\"}")
        .when()
                .post("https://restapi.wcaquino.me/users")
        .then()
                .log().all()
                .statusCode(201)
                .body("id", is(notNullValue()))
                .body("name", is("Dione"))
                .body("age", is("45"))
                .body("salary", is("5960.45"))
        ;
    }

    @Test
    public void deveSalvarUsuarioUsandoMap() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("name", "Dione");
        params.put("age", 45);
        params.put("salary", 5960.45);
        given()
                .log().all()
                .contentType("application/json")
                .body(params)
                .when()
                .post("https://restapi.wcaquino.me/users")
                .then()
                .log().all()
                .statusCode(201)
                .body("id", is(notNullValue()))
                .body("name", is("Dione"))
                .body("age", is(45))
                .body("salary", is(5960.45f))
        ;
    }

    @Test
    public void deveSalvarUsuarioUsandoObjeto() {
        User user = new User("Dione Quevedo", 45);
        given()
                .log().all()
                .contentType("application/json")
                .body(user)
                .when()
                .post("https://restapi.wcaquino.me/users")
                .then()
                .log().all()
                .statusCode(201)
                .body("id", is(notNullValue()))
                .body("name", is("Dione Quevedo"))
                .body("age", is(45))
               // .body("salary", is(5960.45f))
        ;
    }

    @Test
    public void deveDeserializarObjetoAoSalvarUsuario() {
        User user = new User("Sheila Martins", 47);

        User usuarioInserido = given()
                .log().all()
                .contentType("application/json")
                .body(user)
                .when()
                .post("https://restapi.wcaquino.me/users")
                .then()
                .log().all()
                .statusCode(201)
                .extract().body().as(User.class);

//        System.out.println(usuarioInserido);
        Assert.assertEquals("Sheila Martins", usuarioInserido.getName());
        Assert.assertThat(usuarioInserido.getAge(), is(47));
        Assert.assertThat(usuarioInserido.getId(), notNullValue());
    }

    @Test
    public void naoDeveSalvarUsuarioJSonSemNome() {
        given()
                .log().all()
                .contentType("application/json")
                .body("{\"age\":\"45\",\"salary\":\"5960.45\"}")
        .when()
                .post("https://restapi.wcaquino.me/users")
        .then()
                .log().all()
                .statusCode(400)
                .body("id", is(nullValue()))
                .body("error", is("Name é um atributo obrigatório"))
        ;
    }

    @Test
    public void deveSalvarUsuarioXML() {
        given()
                .log().all()
                .contentType("application/xml")
                .body("<user><name>Sheila Martins</name><age>47</age><salary>5960.56</salary></user>")
        .when()
                .post("https://restapi.wcaquino.me/usersXML")
        .then()
                .log().all()
                .statusCode(201)
                .body(hasXPath("/user[@id]"), is(notNullValue()))
                .body(hasXPath(("/user/name"), is("Sheila Martins")))
                .body(hasXPath(("/user/age"), is("47")))
                .body(hasXPath(("/user/salary"), is("5960.56")))
        ;
    }

    @Test
    public void deveSalvarUsuarioSemFilhosXML() {
        given()
                .log().all()
                .contentType("application/xml")
                .body("<user><name>Maria Madu</name><age>7</age><filhos></filhos></user>")
        .when()
                .post("https://restapi.wcaquino.me/usersXML")
        .then()
                .log().all()
                .statusCode(201)
                .body(hasXPath("/user[@id]"), is(notNullValue()))
                .body(hasXPath(("/user/name"), is("Maria Madu")))
                .body(hasXPath(("/user/age"), is("7")))
                .body(hasXPath(("count(/user/filhos)"), is("1")))
        ;
    }

    @Test
    public void naoDeveSalvarUsuarioSemNomeXML() {
        given()
                .log().all()
                .contentType("application/xml")
                .body("<user><name>Ana Julia</name><age>27</age></user>")
                .when()
                .post("https://restapi.wcaquino.me/usersXML")
                .then()
                .log().all()
                .statusCode(201)
                .body("user.@id", is(notNullValue()))
                .body("user.name", is("Ana Julia"))
                .body("user.age", is("27"))
        ;
    }

    @Test
    public void deveALterarUsuarioJSon1() {
        given()
                .log().all()
                .contentType("application/json")
                .body("{\"name\":\"Maria Joaquinas\",\"age\":\"45\",\"salary\":\"5960.45\"}")
                .when()
                .put("https://restapi.wcaquino.me/users/1")
                .then()
                .log().all()
                .statusCode(200)
                .body("id", is(notNullValue()))
                .body("name", is("Maria Joaquinas"))
                .body("age", is("45"))
                .body("salary", is("5960.45"))
        ;
    }

    @Test
    public void naoDeveAlterarUsuarioJSonSemNome() {
        given()
                .log().all()
                .contentType("application/json")
                .body("{\"age\":\"55\",\"salary\":\"5960.45\"}")
                .when()
                .put("https://restapi.wcaquino.me/users/2")
                .then()
                .log().all()
                .statusCode(400)
                .body("error", is("Name é um atributo obrigatório"))
        ;
    }

    @Test
    public void deveAlterarUsuarioJSon2() {
        given()
                .log().all()
                .contentType("application/json")
                .body("{\"name\":\" \",\"age\":\"55\",\"salary\":\"5960.45\"}")
                .when()
                .put("https://restapi.wcaquino.me/users/2")
                .then()
                .log().all()
                .statusCode(200)
                .body("id.toString()", is("2"))
                .body("salary.toString()", is(notNullValue()))
        ;
    }

    @Test
    public void deveCustomizarURLPrimeiraForma() {
        given()
                .log().all()
                .contentType("application/json")
                .body("{\"name\":\"Maria Joaquinas\",\"age\":\"45\",\"salary\":\"5960.45\"}")
                .when()
                .put("https://restapi.wcaquino.me/{entidade}/{userid}", "users", "1")
                .then()
                .log().all()
                .statusCode(200)
        ;
    }

    @Test
    public void deveCustomizarURLSegundaForma() {
        given()
                .log().all()
                .contentType("application/json")
                .body("{\"name\":\"Maria Joaquinas\",\"age\":\"45\",\"salary\":\"5960.45\"}")
                .pathParam("entidade", "users")
                .pathParam("userId", "1")
        .when()
                .put("https://restapi.wcaquino.me/{entidade}/{userId}")
        .then()
                .log().all()
                .statusCode(200)
        ;
    }

    @Test
    public void deveRemoverUmUsuario() {
        given()
                .log().all()
                .contentType("application/json")
                .pathParam("entidade", "users")
                .pathParam("userId", "1")
        .when()
                .delete("https://restapi.wcaquino.me/{entidade}/{userId}")
        .then()
                .log().all()
                .statusCode(204)
        ;
    }

    @Test
    public void naoDeveRemoverUmUsuarioInexistente() {
        given()
                .log().all()
                .contentType("application/json")
                .pathParam("entidade", "users")
                .pathParam("userId", "9")
        .when()
                .delete("https://restapi.wcaquino.me/{entidade}/{userId}")
        .then()
                .log().all()
                .statusCode(400)
                .body("error", is("Registro inexistente"))
        ;
    }
}
