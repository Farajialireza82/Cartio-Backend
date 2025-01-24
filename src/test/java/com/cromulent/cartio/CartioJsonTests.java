package com.cromulent.cartio;

import com.cromulent.cartio.model.ShopItem;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CartioJsonTests {

    @Autowired
    private JacksonTester<ShopItem> json;

    @Autowired
    private JacksonTester<ShopItem[]> jsonList;

    private ShopItem[] shopItems;

    @BeforeEach
    void setUp() {
        shopItems = Arrays.array(
                new ShopItem(1L, "bread", "2", false),
                new ShopItem(2L, "egg", "10", false),
                new ShopItem(3L, "milk", "1", true)
        );
    }

    @Test
    void shopItemSerializationTest() throws IOException {
        ShopItem shopItem = shopItems[0];
        assertThat(json.write(shopItem)).isEqualTo("single_shop_item.json");
        assertThat(json.write(shopItem)).hasJsonPathNumberValue("@.id");
        assertThat(json.write(shopItem)).extractingJsonPathNumberValue("@.id")
                .isEqualTo(1);
        assertThat(json.write(shopItem)).extractingJsonPathStringValue("@.name")
                .isEqualTo("bread");
        assertThat(json.write(shopItem)).extractingJsonPathNumberValue("@.amount")
                .isEqualTo(2);
        assertThat(json.write(shopItem)).extractingJsonPathBooleanValue("@.is_bought")
                .isEqualTo(false);
    }

    @Test
    void shopItemDeserializationTest() throws IOException {
        String expected = """
                {
                  "id": 1,
                  "name": "bread",
                  "amount": 2,
                  "is_bought": false
                }
                """;

        assertThat(json.parse(expected))
                .usingRecursiveComparison().isEqualTo(new ShopItem(1L, "bread", "2", false));
        assertThat(json.parseObject(expected).getId()).isEqualTo(1);
        assertThat(json.parseObject(expected).getAmount()).isEqualTo("2");
        assertThat(json.parseObject(expected).getName()).isEqualTo("bread");
        assertThat(json.parseObject(expected).isBought()).isEqualTo(false);
    }

    @Test
    void shopItemListSerializationTest() throws IOException {
        assertThat(jsonList.write(shopItems)).isStrictlyEqualToJson("list_shop_item.json");
    }
    @Test
    void shopItemListDeserializationTest() throws IOException {
        String expected = """
                [
                  { "id": 1, "name": "bread", "amount": 2,"is_bought": false},
                  { "id": 2, "name": "egg", "amount": 10, "is_bought": false},
                  { "id": 3, "name": "milk", "amount": 1,"is_bought": true}
                ]
                """;
        assertThat(jsonList.parse(expected)).usingRecursiveComparison().isEqualTo(shopItems);
    }

}
