package com.example.ProduitService;

import com.example.ProduitService.dto.ProduitRequest;
import com.example.ProduitService.repository.ProduitRepository;
import com.fasterxml.jackson.databind.ObjectMapper;  // Correct import
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.github.dockerjava.core.MediaType;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class ProduitServiceApplicationTests {

	@Container
	static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:8.0.4");

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;  // Spring's ObjectMapper will be injected
	@Autowired
	private ProduitRepository productRepository;

	static {
		mongoDBContainer.start();
	}

	@DynamicPropertySource
	static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
		dynamicPropertyRegistry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
	}

	@Test
	void shouldCreateProduct() throws Exception {
		ProduitRequest productRequest = getProduitRequest();
		String productRequestString = objectMapper.writeValueAsString(productRequest);

		mockMvc.perform(MockMvcRequestBuilders.post("/api/produit")
						.contentType(String.valueOf(MediaType.APPLICATION_JSON))
						.content(productRequestString))
				.andExpect(status().isCreated());

		Assertions.assertEquals(1, productRepository.findAll().size());
	}

	private ProduitRequest getProduitRequest() {
		return ProduitRequest.builder()
				.nom("iPhone 13")
				.prixUnitaire(100.5)
				.seuilCritique(10)
				.IdStock(String.valueOf(1))
				.dateAjout(parseDate("2025-01-27T00:00:00.000+00:00"))
				.build();
	}

	private Date parseDate(String dateString) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
			return sdf.parse(dateString);
		} catch (ParseException e) {
			throw new RuntimeException("Failed to parse date: " + dateString, e);
		}
	}
}
