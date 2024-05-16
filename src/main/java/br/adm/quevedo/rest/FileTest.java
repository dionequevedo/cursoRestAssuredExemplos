package br.adm.quevedo.rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import io.restassured.http.ContentType;
import org.junit.Assert;
import org.junit.Test;

public class FileTest {
    @Test
    public void deveObrigarEnvioArquivo() {
        given()
                .log().all()
        .when()
                .post("http://restapi.wcaquino.me/upload")
        .then()
                .log().all()
                .statusCode(404)
                .body("error", is("Arquivo não enviado"))
        ;
    }

    @Test
    public void deveFazerUploadDoArquivo() {
        given()
                .log().all()
                .multiPart("arquivo", new File("src/main/resources/users.pdf"))
        .when()
                .post("http://restapi.wcaquino.me/upload")
        .then()
                .log().all()
                .time(lessThan(1000l))
                .statusCode(200)
                .body("name", is("users.pdf"))
        ;
    }

    @Test
    public void naoDeveFazerUploadDeArquivosGrandes() {
        given()
                .log().all()
                .multiPart("arquivo", new File("src/main/resources/iText-2.1.0.jar"))
        .when()
                .post("http://restapi.wcaquino.me/upload")
        .then()
                .log().all()
                .contentType(ContentType.HTML)
                .statusCode(413)
                .body("html.body.center.h1", is("413 Request Entity Too Large"))
        ;
    }

    @Test
    public void naoDeveFazerUploadComMaisDe1_3Seg() {
        given()
                .log().all()
                .multiPart("arquivo", new File("src/main/resources/iText-2.1.0.jar"))
        .when()
                .post("http://restapi.wcaquino.me/upload")
        .then()
                .log().all()
                .time(greaterThan(1000L))
                .contentType(ContentType.HTML)
                .statusCode(413)
                .body("html.body.center.h1", is("413 Request Entity Too Large"))
        ;
    }

    @Test
    public void deveBaixarArquivo() throws IOException{
        byte[] image = given()
            .log().all()
        .when()
                .get("http://restapi.wcaquino.me/download")
        .then()
                .statusCode(200)
                .extract().asByteArray()
        ;
        File imagem = new File("src/main/resources/minhaImagem.jpg");
        OutputStream os = new FileOutputStream(imagem);
        os.write(image);
        os.close();

        Assert.assertThat(imagem.length(), lessThanOrEqualTo(94878L));
        Assert.assertThat(imagem.getName(), is("minhaImagem.jpg"));
    }
}
