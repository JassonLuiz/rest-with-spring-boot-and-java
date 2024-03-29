package io.github.jassonluiz.restwithspringbootandjava.integrationtests.swagger;

import static io.restassured.RestAssured.given;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import io.github.jassonluiz.restwithspringbootandjava.configs.TestConfig;
import io.github.jassonluiz.restwithspringbootandjava.integrationtests.testscontainers.AbstractIntegrationTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class SwaggerIntegrationTest extends AbstractIntegrationTest{

	@Test
	public void shouldDisplaySwaggerUiPage() {
		var content =
				given()
					.basePath("/swagger-ui/index.html")
					.port(TestConfig.SERVER_PORT)
					.when()
						.get()
					.then()
						.statusCode(200)
					.extract()
						.body()
							.asString();
		
		assertTrue(content.contains("Swagger UI"));
	}

}
