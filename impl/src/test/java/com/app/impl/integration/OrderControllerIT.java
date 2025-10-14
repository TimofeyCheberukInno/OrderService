package com.app.impl.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.junit.jupiter.api.TestInstance;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.app.impl.domain.OrderStatus;
import com.app.impl.dto.order.OrderRequestDto;
import com.app.impl.dto.order.OrderUpdateRequestDto;
import com.app.impl.dto.orderItem.OrderItemRequestDto;
import com.app.impl.dto.user.UserResponseDto;
import com.app.impl.entity.Item;
import com.app.impl.repository.ItemRepository;
import com.app.impl.repository.OrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.app.impl.integration.config.TestcontainersConfig;

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
@AutoConfigureMockMvc
@Import({ TestcontainersConfig.class })
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OrderControllerIT {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private static WireMockServer wireMockServer;

    @AfterAll
    void stopWireMock() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        if (wireMockServer == null) {
            wireMockServer = new WireMockServer(com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig().dynamicPort());
            wireMockServer.start();
        }
        registry.add("user.service.url", () -> wireMockServer.baseUrl());
    }

    @BeforeEach
    void setup() {
        orderRepository.deleteAll();
        itemRepository.deleteAll();
        wireMockServer.resetAll();
        WireMock.configureFor("localhost", wireMockServer.port());
    }

    private UserResponseDto stubUser(String email) throws Exception {
        UserResponseDto user = new UserResponseDto(1L, "John", "Doe", java.time.LocalDate.of(1990, 1, 1), email);
        String body = objectMapper.writeValueAsString(user);
        wireMockServer.stubFor(
                WireMock.get(
                                WireMock.urlPathEqualTo("/api/users/by-email"))
                        .withQueryParam("email", WireMock.equalTo(email))
                        .willReturn(
                                WireMock.aResponse()
                                        .withStatus(200)
                                        .withHeader("Content-Type", "application/json")
                                        .withBody(body)
                        )
        );
        return user;
    }

    private Item seedItem(long idHint, String name, BigDecimal price) {
        return itemRepository.save(new Item(null, name, price));
    }

    @Nested
    @DisplayName("Tests for POST /api/orders")
    class CreateOrderTests {
        @Test
        @DisplayName("return 201 status on valid request")
        void shouldCreateOrder() throws Exception {
            Item i1 = seedItem(1, "item_1", BigDecimal.valueOf(10.00));
            Item i2 = seedItem(2, "item_2", BigDecimal.valueOf(20.00));
            stubUser("user@example.com");

            OrderRequestDto request = new OrderRequestDto(
                    "user@example.com",
                    List.of(
                            new OrderItemRequestDto(i1.getId(), 2),
                            new OrderItemRequestDto(i2.getId(), 1)
                    )
            );

            mockMvc.perform(post("/api/orders")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("return 400 status on invalid request body")
        void shouldReturnBadRequestOnInvalidCreate() throws Exception {
            OrderRequestDto request = new OrderRequestDto(
                    null,
                    null
            );

            mockMvc.perform(post("/api/orders")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Tests for PUT /api/orders")
    class UpdateOrderTests {
        @Test
        @DisplayName("return 200 status on valid update")
        void shouldUpdateOrder() throws Exception {
            Item i1 = seedItem(1, "item_1", BigDecimal.valueOf(10.00));
            stubUser("user@example.com");

            OrderRequestDto create = new OrderRequestDto(
                    "user@example.com",
                    List.of(new OrderItemRequestDto(i1.getId(), 1))
            );

            String createJson = objectMapper.writeValueAsString(create);
            mockMvc.perform(post("/api/orders").contentType(MediaType.APPLICATION_JSON).content(createJson))
                    .andExpect(status().isCreated());

            Long orderId = orderRepository.findAll().get(0).getId();

            OrderUpdateRequestDto update = new OrderUpdateRequestDto(orderId, OrderStatus.COMPLETED);

            mockMvc.perform(put("/api/orders")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(update)))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("return 400 status on invalid request body")
        void shouldReturnBadRequestOnInvalidUpdate() throws Exception {
            OrderUpdateRequestDto update = new OrderUpdateRequestDto(
                    null,
                    null
            );

            mockMvc.perform(put("/api/orders")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(update)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Tests for GET /api/orders/{id}")
    class GetOrderByIdTests {
        @Test
        @DisplayName("return 200 status when order exists")
        void shouldReturnOrderById() throws Exception {
            Item i1 = seedItem(1, "item_1", BigDecimal.valueOf(10.00));
            stubUser("user@example.com");
            OrderRequestDto create = new OrderRequestDto(
                    "user@example.com",
                    List.of(new OrderItemRequestDto(i1.getId(), 1))
            );
            mockMvc.perform(post("/api/orders").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(create)))
                    .andExpect(status().isCreated());
            Long orderId = orderRepository.findAll().get(0).getId();

            mockMvc.perform(get("/api/orders/" + orderId))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("return 400 status when order not found")
        void shouldReturnBadRequestWhenNotFound() throws Exception {
            mockMvc.perform(get("/api/orders/999999"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("return 400 status when id is invalid")
        void shouldReturnBadRequestWhenIdInvalid() throws Exception {
            mockMvc.perform(get("/api/orders/0"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Tests for GET /api/orders?ids=")
    class GetOrdersByIdsTests {
        @Test
        @DisplayName("return 200 status when all orders exist")
        void shouldReturnOrdersByIds() throws Exception {
            Item i1 = seedItem(1, "item_1", BigDecimal.valueOf(10.00));
            Item i2 = seedItem(2, "item_2", BigDecimal.valueOf(20.00));
            stubUser("user@example.com");

            OrderRequestDto r1 = new OrderRequestDto("user@example.com", List.of(new OrderItemRequestDto(i1.getId(), 1)));
            OrderRequestDto r2 = new OrderRequestDto("user@example.com", List.of(new OrderItemRequestDto(i2.getId(), 2)));
            mockMvc.perform(post("/api/orders").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(r1)))
                    .andExpect(status().isCreated());
            mockMvc.perform(post("/api/orders").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(r2)))
                    .andExpect(status().isCreated());

            List<Long> ids = orderRepository.findAll().stream().map(o -> o.getId()).toList();

            mockMvc.perform(get("/api/orders").queryParam("ids", ids.stream().map(String::valueOf).toArray(String[]::new)))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("return 400 status when some order ids not found")
        void shouldReturnBadRequestWhenSomeIdsMissing() throws Exception {
            Item i1 = seedItem(1, "item_1", BigDecimal.valueOf(10.00));
            stubUser("user@example.com");

            OrderRequestDto r1 = new OrderRequestDto("user@example.com", List.of(new OrderItemRequestDto(i1.getId(), 1)));
            mockMvc.perform(post("/api/orders").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(r1)))
                    .andExpect(status().isCreated());

            Long id = orderRepository.findAll().get(0).getId();

            mockMvc.perform(get("/api/orders").queryParam("ids", String.valueOf(id), "999999"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Tests for GET /api/orders/by-status?status=")
    class GetOrdersByStatusTests {
        @Test
        @DisplayName("return 200 status with list of orders by status")
        void shouldReturnOrdersByStatus() throws Exception {
            Item i1 = seedItem(1, "item_1", BigDecimal.valueOf(10.00));
            stubUser("user@example.com");

            OrderRequestDto r1 = new OrderRequestDto("user@example.com", List.of(new OrderItemRequestDto(i1.getId(), 1)));
            mockMvc.perform(post("/api/orders").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(r1)))
                    .andExpect(status().isCreated());

            mockMvc.perform(get("/api/orders/by-status").queryParam("status", OrderStatus.IN_PROCESS.name()))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("return 200 status and empty list when none matches")
        void shouldReturnEmptyListWhenNoOrdersForStatus() throws Exception {
            mockMvc.perform(get("/api/orders/by-status").queryParam("status", OrderStatus.CANCELLED.name()))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("Tests for GET /api/orders")
    class GetAllOrdersTests {
        @Test
        @DisplayName("return 200 status and empty list when no orders")
        void shouldReturnEmptyList() throws Exception {
            mockMvc.perform(get("/api/orders"))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("return 200 status and non-empty list")
        void shouldReturnNonEmptyList() throws Exception {
            Item i1 = seedItem(1, "item_1", BigDecimal.valueOf(10.00));
            stubUser("user@example.com");
            OrderRequestDto r1 = new OrderRequestDto("user@example.com", List.of(new OrderItemRequestDto(i1.getId(), 1)));
            mockMvc.perform(post("/api/orders").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(r1)))
                    .andExpect(status().isCreated());

            mockMvc.perform(get("/api/orders"))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("Tests for DELETE /api/orders/{id}")
    class DeleteOrderTests {
        @Test
        @DisplayName("return 200 status on delete")
        void shouldDeleteOrder() throws Exception {
            Item i1 = seedItem(1, "item_1", BigDecimal.valueOf(10.00));
            stubUser("user@example.com");
            OrderRequestDto r1 = new OrderRequestDto("user@example.com", List.of(new OrderItemRequestDto(i1.getId(), 1)));
            mockMvc.perform(post("/api/orders").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(r1)))
                    .andExpect(status().isCreated());
            Long orderId = orderRepository.findAll().get(0).getId();

            mockMvc.perform(delete("/api/orders/" + orderId))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("return 400 status when id is invalid")
        void shouldReturnBadRequestOnInvalidId() throws Exception {
            mockMvc.perform(delete("/api/orders/0"))
                    .andExpect(status().isBadRequest());
        }
    }
}


