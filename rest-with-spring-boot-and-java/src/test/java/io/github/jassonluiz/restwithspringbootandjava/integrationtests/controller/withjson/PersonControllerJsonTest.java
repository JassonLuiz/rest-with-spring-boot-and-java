package io.github.jassonluiz.restwithspringbootandjava.integrationtests.controller.withjson;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder.SecretKeyFactoryAlgorithm;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.jassonluiz.restwithspringbootandjava.configs.TestConfig;
import io.github.jassonluiz.restwithspringbootandjava.integrationtests.testscontainers.AbstractIntegrationTest;
import io.github.jassonluiz.restwithspringbootandjava.integrationtests.vo.AccountCredentialsVO;
import io.github.jassonluiz.restwithspringbootandjava.integrationtests.vo.PersonVO;
import io.github.jassonluiz.restwithspringbootandjava.integrationtests.vo.TokenVO;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(OrderAnnotation.class)
public class PersonControllerJsonTest extends AbstractIntegrationTest{

	private static RequestSpecification specification;
	private static ObjectMapper objectMapper;
	
	private static PersonVO person;
	
	@BeforeAll
	public static void setup() {
		objectMapper = new ObjectMapper();
		objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		
		person = new PersonVO();
	}
	
	@Test
	@Order(0)
	public void authorization() throws JsonMappingException, JsonProcessingException {
		AccountCredentialsVO user = new AccountCredentialsVO("leandro", "admin123");
		
		var accessToken = given()
				.basePath("/auth/signin")
					.port(TestConfig.SERVER_PORT)
					.contentType(TestConfig.CONTENT_TYPE_JSON)
				.body(user)
					.when()
				.post()
					.then()
						.statusCode(200)
							.extract()
							.body()
								.as(TokenVO.class)
							.getAccessToken();
		
		specification = new RequestSpecBuilder()
				.addHeader(TestConfig.HEADER_PARAM_AUTHORIZATION, "Bearer " + accessToken)
				.setBasePath("/api/v1/person")
				.setPort(TestConfig.SERVER_PORT)
					.addFilter(new RequestLoggingFilter(LogDetail.ALL))
					.addFilter(new ResponseLoggingFilter(LogDetail.ALL))
				.build();
		
	}
	
	@Test
	@Order(1)
	public void testCreate() throws JsonMappingException, JsonProcessingException {
		mockPerson();
		
		var content = given().spec(specification)
				.contentType(TestConfig.CONTENT_TYPE_JSON)
						.header(TestConfig.HEADER_PARAM_ORIGIN, "http://localhost:8080")
						.body(person)
						.when()
						.post()
					.then()
						.statusCode(200)
					.extract()
						.body()
							.asString();
		
		PersonVO persistedPerson = objectMapper.readValue(content, PersonVO.class);
		person = persistedPerson;
		
		assertNotNull(persistedPerson);
		assertNotNull(persistedPerson.getId());
		assertNotNull(persistedPerson.getFirstName());
		
		assertTrue(persistedPerson.getId() > 0);
		
		assertEquals("Lucas", persistedPerson.getFirstName());
		assertEquals("Silva", persistedPerson.getLastName());
	}
	
	@Test
	@Order(2)
	public void testCreateWithWrongOrigin() throws JsonMappingException, JsonProcessingException {
		mockPerson();
		
		var content = given().spec(specification)
				.contentType(TestConfig.CONTENT_TYPE_JSON)
					.header(TestConfig.HEADER_PARAM_ORIGIN, TestConfig.ORIGIN_ERRO)
					.body(person)
				.when()
					.post()
				.then()
					.statusCode(403)
					.extract()
					.body()
						.asString();
		
		assertNotNull(content);
		assertEquals("Invalid CORS request", content);
	}
	
	@Test
	@Order(3)
	public void testUpdate() throws JsonMappingException, JsonProcessingException {
		person.setLastName("Piquet Souto Maior");
		
		var content = given().spec(specification)
				.contentType(TestConfig.CONTENT_TYPE_JSON)
					.body(person)
					.when()
					.post()
				.then()
					.statusCode(200)
						.extract()
						.body()
							.asString();
		
		PersonVO persistedPerson = objectMapper.readValue(content, PersonVO.class);
		person = persistedPerson;
		
		assertNotNull(persistedPerson);
		
		assertNotNull(persistedPerson.getId());
		assertNotNull(persistedPerson.getFirstName());
		assertNotNull(persistedPerson.getLastName());
		assertNotNull(persistedPerson.getAddress());
		assertNotNull(persistedPerson.getGender());
		assertTrue(persistedPerson.getEnabled());
		
		assertEquals(person.getId(), persistedPerson.getId());
		
		assertEquals("Nelson", persistedPerson.getFirstName());
		assertEquals("Piquet Souto Maior", persistedPerson.getLastName());
		assertEquals("Brasília - DF - Brasil", persistedPerson.getAddress());
		assertEquals("Male", persistedPerson.getGender());
	}
	
	@Test
	@Order(4)
	public void testDisablePersonById() throws JsonMappingException, JsonProcessingException {
		
		var content = given().spec(specification)
				.contentType(TestConfig.CONTENT_TYPE_JSON)
						.header(TestConfig.HEADER_PARAM_ORIGIN, "http://localhost:8080")
						.pathParam("id", person.getId())
						.when()
						.patch("{id}")
					.then()
						.statusCode(200)
					.extract()
						.body()
							.asString();
		
		PersonVO persistedPerson = objectMapper.readValue(content, PersonVO.class);
		person = persistedPerson;
		
		assertNotNull(persistedPerson);
		assertNotNull(persistedPerson.getId());
		assertNotNull(persistedPerson.getFirstName());
		assertFalse(persistedPerson.getEnabled());
		
		assertTrue(persistedPerson.getId() > 0);
		
		assertEquals("Lucas", persistedPerson.getFirstName());
		assertEquals("Silva", persistedPerson.getLastName());
	}
	
	@Test
	@Order(5)
	public void testFindById() throws JsonMappingException, JsonProcessingException {
		mockPerson();
		
		var content = given().spec(specification)
				.contentType(TestConfig.CONTENT_TYPE_JSON)
						.header(TestConfig.HEADER_PARAM_ORIGIN, "http://localhost:8080")
						.pathParam("id", person.getId())
						.when()
						.get("{id}")
					.then()
						.statusCode(200)
					.extract()
						.body()
							.asString();
		
		PersonVO persistedPerson = objectMapper.readValue(content, PersonVO.class);
		person = persistedPerson;
		
		assertNotNull(persistedPerson);
		assertNotNull(persistedPerson.getId());
		assertNotNull(persistedPerson.getFirstName());
		
		assertTrue(persistedPerson.getId() > 0);
		
		assertEquals("Lucas", persistedPerson.getFirstName());
		assertEquals("Silva", persistedPerson.getLastName());
	}
	
	
	
	@Test
	@Order(6)
	public void testFindByIdWithWrongOrigin() throws JsonMappingException, JsonProcessingException {
		mockPerson();
		
		var content = given().spec(specification)
				.contentType(TestConfig.CONTENT_TYPE_JSON)
					.header(TestConfig.HEADER_PARAM_ORIGIN, "http://localhost:8080")
					.pathParam("id", person.getId())
				.when()
					.get("{id}")
					.then()
						.statusCode(403)
						.extract()
						.body()
							.asString();
		
		assertNotNull(content);
		assertEquals("Invalid CORS request", content);
	}

	private void mockPerson() {
		person.setFirstName("Lucas");
		person.setLastName("Silva");
		person.setAddress("Recife, Pernambuco, BR");
		person.setGender("Male");
		person.setEnabled(true);
	}

}
