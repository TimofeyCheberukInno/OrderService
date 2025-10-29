package com.app.impl.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;


import com.app.impl.dto.item.ItemRequestDto;
import com.app.impl.dto.item.ItemUpdateRequestDto;
import com.app.impl.entity.Item;
import com.app.impl.repository.ItemRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.app.impl.integration.config.TestcontainersConfig;

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
@AutoConfigureMockMvc
@Import({ TestcontainersConfig.class })
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ItemControllerIT {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ObjectMapper objectMapper;
    
    @BeforeEach
    void setup() {
        itemRepository.deleteAll();
    }

    @Nested
    @DisplayName("Tests for POST /api/items")
    class CreateItemTests {
        @Test
        @DisplayName("return 201 status on valid request")
        void shouldCreateItem() throws Exception {
            ItemRequestDto request = new ItemRequestDto(
                    "item_1",
                    BigDecimal.valueOf(123.45)
            );

            mockMvc.perform(post("/api/items")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("return 400 status on invalid request body")
        void shouldReturnBadRequestOnInvalidCreate() throws Exception {
            ItemRequestDto request = new ItemRequestDto(
                    null,
                    null
            );

            mockMvc.perform(post("/api/items")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Tests for PUT /api/items")
    class UpdateItemTests {
        @Test
        @DisplayName("return 200 status on valid update")
        void shouldUpdateItem() throws Exception {
            Item saved = itemRepository.save(new Item(
                    null,
                    "item_1",
                    BigDecimal.valueOf(10.00)
            ));

            ItemUpdateRequestDto request = new ItemUpdateRequestDto(
                    saved.getId(),
                    "item_1_updated",
                    BigDecimal.valueOf(20.50)
            );

            mockMvc.perform(put("/api/items")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("return 400 status on invalid request body")
        void shouldReturnBadRequestOnInvalidUpdate() throws Exception {
            ItemUpdateRequestDto request = new ItemUpdateRequestDto(
                    null,
                    null,
                    null
            );

            mockMvc.perform(put("/api/items")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Tests for GET /api/items/{id}")
    class GetItemByIdTests {
        @Test
        @DisplayName("return 200 status when item exists")
        void shouldReturnItemById() throws Exception {
            Item saved = itemRepository.save(new Item(
                    null,
                    "item_1",
                    BigDecimal.valueOf(10.00)
            ));

            mockMvc.perform(get("/api/items/" + saved.getId()))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("return 400 status when item not found")
        void shouldReturnBadRequestWhenNotFound() throws Exception {
            mockMvc.perform(get("/api/items/999999"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("return 400 status when id is invalid")
        void shouldReturnBadRequestWhenIdInvalid() throws Exception {
            mockMvc.perform(get("/api/items/0"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Tests for GET /api/items?ids=")
    class GetItemsByIdsTests {
        @Test
        @DisplayName("return 200 status when all items exist")
        void shouldReturnItemsByIds() throws Exception {
            Item item1 = itemRepository.save(new Item(null, "item_1", BigDecimal.valueOf(10.00)));
            Item item2 = itemRepository.save(new Item(null, "item_2", BigDecimal.valueOf(20.00)));

            mockMvc.perform(get("/api/items")
                            .queryParam("ids", item1.getId().toString(), item2.getId().toString()))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("return 400 status when some item ids not found")
        void shouldReturnBadRequestWhenSomeIdsMissing() throws Exception {
            Item item1 = itemRepository.save(new Item(null, "item_1", BigDecimal.valueOf(10.00)));

            mockMvc.perform(get("/api/items")
                            .queryParam("ids", item1.getId().toString(), "999999"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Tests for GET /api/items")
    class GetAllItemsTests {
        @Test
        @DisplayName("return 200 status and empty list when no items")
        void shouldReturnEmptyList() throws Exception {
            mockMvc.perform(get("/api/items"))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("return 200 status and non-empty list")
        void shouldReturnNonEmptyList() throws Exception {
            itemRepository.save(new Item(null, "item_1", BigDecimal.valueOf(10.00)));
            itemRepository.save(new Item(null, "item_2", BigDecimal.valueOf(20.00)));

            mockMvc.perform(get("/api/items"))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("Tests for DELETE /api/items/{id}")
    class DeleteItemTests {
        @Test
        @DisplayName("return 200 status on delete")
        void shouldDeleteItem() throws Exception {
            Item saved = itemRepository.save(new Item(null, "item_1", BigDecimal.valueOf(10.00)));

            mockMvc.perform(delete("/api/items/" + saved.getId()))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("return 400 status when id is invalid")
        void shouldReturnBadRequestOnInvalidId() throws Exception {
            mockMvc.perform(delete("/api/items/0"))
                    .andExpect(status().isBadRequest());
        }
    }
}


