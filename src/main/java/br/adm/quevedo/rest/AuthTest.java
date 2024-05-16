package br.adm.quevedo.rest;

import io.restassured.http.ContentType;
import io.restassured.path.xml.XmlPath;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class AuthTest {
    @Test
    public void deveAcessarSWAPI() {
        given()
                .log().all()
        .when()
                .get("https://swapi.dev/api/people/1")
        .then()
                .log().all()
                .statusCode(200)
                .contentType("application/json")
                .body("name", is("Luke Skywalker"))

        ;
    }

    @Test
    public void deveAcessarOpenWeatherMap() {
        //    fb3ae258bc9ad0c7d7ffc0d191c6b6ed
        //    2711ae267510735f0266fb94cde2cb8e
        given()
                .log().all()
                .queryParam("q", "Porto Alegre,RS,BR")
                .queryParam("appid", "fb3ae258bc9ad0c7d7ffc0d191c6b6ed")
                .queryParam("units", "metric")
        .when()
                .get("https://api.openweathermap.org/data/2.5/weather")
        .then()
                .log().all()
                .statusCode(200)
                .contentType("application/json")
                .body("name", is("Porto Alegre"))
                .body("main.temp", is(greaterThan(0f)))
                .body("main.temp", is(lessThan(40f)))
                .body("sys.country", is("BR"))
        ;
    }

    @Test
    public void naoDeveAcessarSemSenha() {
        given()
                .log().all()
                .when()
                .get("https://restapi.wcaquino.me/basicauth")
                .then()
                .log().all()
                .statusCode(401)
                .contentType(is("text/html; charset=utf-8"))
        ;
    }

    @Test
    public void deveFazerAutenticacaoBasica() {
        given()
                .log().all()
        .when()
                .get("https://admin:senha@restapi.wcaquino.me/basicauth")
        .then()
                .log().all()
                .statusCode(200)
                .contentType(is("application/json; charset=utf-8"))
                .body("status", is("logado"))
        ;
    }

    @Test
    public void deveFazerAutenticacaoBasica2() {
        given()
                .log().all()
                .auth().basic("admin", "senha")
        .when()
                .get("https://restapi.wcaquino.me/basicauth")
        .then()
                .log().all()
                .statusCode(200)
                .contentType(is("application/json; charset=utf-8"))
                .body("status", is("logado"))
        ;
    }

    @Test
    public void deveFazerAutenticacaoBasicaChallenge() {
        given()
                .log().all()
                .auth().preemptive().basic("admin", "senha")
        .when()
                .get("https://restapi.wcaquino.me/basicauth2")
        .then()
                .log().all()
                .statusCode(200)
                .contentType(is("application/json; charset=utf-8"))
                .body("status", is("logado"))
        ;
    }

    @Test
    public void deveFazerAutenticacaoComTokenJWT() {
        Map<String, String> login = new HashMap<String, String>();
        login.put("email", "test@dionequevedo.com.br");
        login.put("senha", "123456");

        // Login na API
        // Receber o token
        String token =
        given()
                .body(login)
                .contentType(ContentType.JSON)
        .when()
                .post("https://barrigarest.wcaquino.me/signin")
        .then()
                .statusCode(200)
                .contentType(is("application/json; charset=utf-8"))
                .body("nome", is("Dione"))
                .extract().path("token")
        ;

        // Obter contas

        given()
                .log().all()
                .header("Authorization", "JWT " + token)
        .when()
                .get("https://barrigarest.wcaquino.me/contas")
        .then()
                .log().all()
                .statusCode(200)
                .contentType(is("application/json; charset=utf-8"))
                .body("nome", hasItem("Conta de teste"))
        ;
    }

    @Test
    public void deveAcessarAplicacaoWeb(){
        // login na página
        String cookie =
        given()
                //.log().all()
                .formParam("email", "test@dionequevedo.com.br")
                .formParam("senha", "123456")
                .contentType(ContentType.URLENC.withCharset("UTF-8"))
        .when()
                .post("https://seubarriga.wcaquino.me/logar")
        .then()
                //.log().all()
                .statusCode(200)
                .extract().header("set-cookie")
        ;

        // System.out.println("cookie completo = " + cookie);
        String cookieTratado = cookie.split("=")[1].split(";")[0];
        // System.out.println("Cookie após tratamento = " + cookieTratado);

        // Acesso a lista de contas
        String conteudoBody =
        given()
                //.log().all()
                .cookie("connect.sid", cookieTratado)
        .when()
                .get("https://seubarriga.wcaquino.me/contas")
        .then()
                //.log().all()
                .statusCode(200)
                .contentType(is("text/html; charset=utf-8"))
                .body("html.body.table.tbody.tr[0].td[0]", is("Conta de teste"))
                .extract().body().asString();
        ;

        System.out.println("- - - - - - - - - - - - - - - - - - -\n");
        XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, conteudoBody);
        System.out.println(xmlPath.getString("html.body.table.tbody.tr[0].td[0]"));

    }
}
