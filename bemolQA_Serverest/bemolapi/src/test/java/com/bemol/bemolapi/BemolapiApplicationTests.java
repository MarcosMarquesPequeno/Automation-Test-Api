package com.bemol.bemolapi;


import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.github.javafaker.Faker;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import static org.hamcrest.Matchers.*;

import static io.restassured.RestAssured.given;

@SpringBootTest
class BemolapiApplicationTests {

	public static final String BASE_URL = "https://serverest.dev";

	Faker faker = new Faker();

	@Test
	public void registerUsersAndValidation() {
		String requestBody = "{\r\n"
				+ "  \"nome\": \"Pedrin Carreteiro\",\r\n"
				+ "  \"email\": \"" + faker.name().username() + "@qa.com\",\r\n"
				+ "  \"password\": \"teste@123\",\r\n"
				+ "  \"administrador\": \"true\"\r\n"
				+ "}";
		String _id = given()
				.header("Content-type", "application/json")
				.body(requestBody)
				.when()
				.post(BASE_URL + "/usuarios")
				.then()
				.statusCode(201)
				.body("message", is("Cadastro realizado com sucesso"))
				.extract().path("_id");

		// Buscando o usuário cadastrado e validando que o mesmo está na lista de
		// usuários
		given()
				.when()
				.get(BASE_URL + "/usuarios?_id=" + _id)
				.then()
				.statusCode(200)
				.body("usuarios.nome[0]", containsString("Pedrin Carreteiro"));
	}

	@Test
	public void registerProductAndCheckIfItWasInserted() {

		//login com user admin 
		String token = given()
				.body("{\n"
						+ "  \"email\": \"fulano@qa.com\",\n"
						+ "  \"password\": \"teste\" \n}")
				.header("Content-type", "application/json")
				.when()
				.post(BASE_URL + "/login")
				.then()
				.statusCode(200)
				.body("message", is("Login realizado com sucesso"))
				.extract().path("authorization");

	
		ExtractableResponse<Response> response = given()
				.header("Authorization", token)
				.header("Content-type", "application/json")
				.body("{ \"nome\": \"Action figure do Homem2222122232" + "\", \"preco\": \"9999"
						+ "\", \"descricao\": \"Action Figure de Pokemon\", \"quantidade\": \"999\" }")
				.when()
				.post(BASE_URL + "/produtos")
				.then()
				.statusCode(201)
				.body("message", is("Cadastro realizado com sucesso"))
				.extract();

		//armazenando o _id que foi extraido do response para assim verificar se o produto foi adicionado
		String product_id = response.path("_id");
		given()
				.when()
				.get(BASE_URL + "/produtos?_id=" + product_id)
				.then()
				.statusCode(200)
				.body("produtos.nome", hasItem("Action figure do Homem2222122232"));

	}

	
}
