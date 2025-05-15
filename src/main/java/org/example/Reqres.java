package org.example;
import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

import io.restassured.http.ContentType;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;


public class Reqres {

    @BeforeClass
    public void setup() {
        baseURI = "https://reqres.in";
        basePath = "/api";
        requestSpecification = given().header("x-api-key", "reqres-free-v1");
    }

    // Listar usuários da página 2 e validar status code 200, número total e quantidade de usuários
    @Test
    public void listarUsuariosDaPagina2() {
        //Header Colocado antes de fazer o RequestSpecification
        given().header("x-api-key", "reqres-free-v1")
                .queryParam("page", 2)
                .when().get("/users").then()
                .body("total", equalTo(12), "data", not(empty()), "page", equalTo(2))
                .statusCode(200).log().all();
    }

    //Buscar um único usuário existente (ex: id = 2) e validar nome e email.
    @Test
    public void buscarUmUnicoUsuarioExistente() {
        requestSpecification.when().get("users/6").then()
                .body("data.email", equalTo("tracey.ramos@reqres.in"), "data.first_name", equalTo("Tracey"))
                .statusCode(200).log().all();
    }

    // Buscar um usuário inexistente (ex: id = 23) e validar status code 404
    @Test
    public void buscarUmUsuarioInexistente() {
        requestSpecification.when().get("users/1441414").then().statusCode(404).log().all();
    }

    // Listar recursos (colors) e validar que a lista não está vazia.
    @Test
    public void listarRecursos() {
        requestSpecification.when().get("unknown").then()
                .body("data", not(emptyArray()))
                .statusCode(200).log().all();
    }

    // Buscar um único recurso existente (ex: id = 2) e validar cor e nome.
    @Test
    public void buscarUmUnicoRecuursoExistente() {
        requestSpecification.when().get("unknown/2").then()
                .body("data.color", equalTo("#C74375"), "data.name", equalTo("fuchsia rose"))
                .statusCode(200).log().all();
    }

    // Buscar um recurso inexistente (ex: id = 23) e validar status code 404.
    @Test
    public void buscarUmRecuursoInexistente() {
        requestSpecification.when().get("unknown/23").then().statusCode(404).log().all();
    }

    // Criar um usuário com nome e job válidos e validar status 201 e dados no corpo da resposta.
    @Test
    public void criarUsuarioComNomeEJob() {
        CreateUser criarUsuario = new CreateUser();
        criarUsuario.setName("Siegh");
        criarUsuario.setJob("Royal Paladin");

        requestSpecification.body(criarUsuario)
                .contentType(ContentType.JSON).when()
                .post("/users").then().statusCode(201).log().all();
    }

    // Tentar criar um usuário com corpo vazio e validar status 400 (se aplicável).
    //API Permite Criar Usuário com nome e Corpo Vazio.
    @Test
    public void tentarCriarUmUsuarioComCorpoVazio() {
        CreateUser criarUsuario = new CreateUser();
        criarUsuario.setName("");
        criarUsuario.setJob("");

        requestSpecification.body(criarUsuario)
                .contentType(ContentType.JSON).when()
                .post("/users").then().statusCode(201).log().all();
    }

    //  Realizar login com email e senha válidos e validar token retornado.
    @Test
    public void realizarLoginComEmailESenhaValidos() {
        Login login = new Login();
        login.setEmail("eve.holt@reqres.in");
        login.setPassword("cityslicka");

        requestSpecification.body(login)
                .contentType(ContentType.JSON).when()
                .post("/login").then()
                .body("token", equalTo("QpwL5tke4Pnpja7X4"))
                .statusCode(200).log().all();
    }

    //  Realizar login com email válido e senha ausente e validar mensagem de erro.
    @Test
    public void realizarLoginComEmailValidoSenhaAusente() {
        Login login = new Login();
        login.setEmail("peter@klaven");
        login.setPassword("");

        requestSpecification.body(login)
                .contentType(ContentType.JSON).when()
                .post("/login").then()
                .body("error", equalTo("Missing password"))
                .statusCode(400).log().all();
    }

    // Realizar registro com email e senha válidos e validar token retornado.
    @Test
    public void realizarRegistroComEmailESenhaValidos() {
        Login login = new Login();
        login.setEmail("eve.holt@reqres.in");
        login.setPassword("pistol");

        requestSpecification.body(login)
                .contentType(ContentType.JSON).when()
                .post("/register").then()
                .body("token", equalTo("QpwL5tke4Pnpja7X4"))
                .statusCode(200).log().all();
        //Validação Hardcoded é melhor Verificar se o token existe e não está vazio
        // .body("token", not(emptyOrNullString()));
    }

    // Realizar registro com email válido e senha ausente e validar mensagem de erro.
    @Test
    public void realizarRegistroComEmailValidoESenhaAusente() {
        Login login = new Login();
        login.setEmail("sydney@fife");
        login.setPassword("");

        requestSpecification.body(login)
                .contentType(ContentType.JSON).when()
                .post("/register").then()
                .body("error", equalTo("Missing password"))
                .statusCode(400).log().all();
    }

    // Fazer atualização parcial de um usuário existente e validar os campos alterados.
    @Test
    public void fazerAtualizacaoParcialDeUmUsuario() {
        CreateUser user = new CreateUser();
        user.setName("caiosiegh");
        user.setJob("RP");

        requestSpecification.body(user)
                .contentType(ContentType.JSON).when()
                .patch("/users/700").then()
                .body("name", equalTo(user.getName()), "job", equalTo(user.getJob()))
                .statusCode(200).log().all();
    }

    // Tentar fazer PATCH em um usuário inexistente e validar resposta.
    //API retorna falso Positivo
    @Test
    public void fazerPatchEmUmUsuarioInexistente() {
        CreateUser user = new CreateUser();
        user.setName("caiosiegh");
        user.setJob("RP");

        requestSpecification.body(user)
                .contentType(ContentType.JSON).when()
                .patch("/users/shdfhlsf").then()
                .statusCode(200).log().all();
    }

    // Deletar um usuário existente (ex: id = 2) e validar status 204.
    @Test
    public void deletarUsuarioExistente() {

        requestSpecification.when()
                .delete("/users/2").then()
                .statusCode(204).log().all();
    }

    // Tentar deletar um usuário inexistente e validar comportamento esperado
    @Test
    public void tentarDeletarUsuarioInexistente() {

        requestSpecification.when()
                .delete("/users/invalidID").then()
                .statusCode(204).log().all();
    }

}