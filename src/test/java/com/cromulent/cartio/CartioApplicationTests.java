package com.cromulent.cartio;

import com.cromulent.cartio.model.ShopItem;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CartioApplicationTests {

	@Autowired
	TestRestTemplate restTemplate;

	@Test
	@DirtiesContext
	void shouldCreateANewShopItem(){
		ShopItem newShopItem = new ShopItem(null, "bread", "5", false);
		ResponseEntity<Void> createResponse = restTemplate
				.postForEntity("/shopItems", newShopItem, Void.class);
		assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
	}

	@Test
	void shouldReturnAllShopItemsWhenListIsRequested() {
		ResponseEntity<String> response = restTemplate
				.getForEntity("/shopItems", String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext documentContext = JsonPath.parse(response.getBody());
		int shopItemCount = documentContext.read("$.length()");
		assertThat(shopItemCount).isEqualTo(3);

		JSONArray amounts = documentContext.read("$..amount");
		assertThat(amounts).containsExactlyInAnyOrder(2, 10, 1);
	}

}
