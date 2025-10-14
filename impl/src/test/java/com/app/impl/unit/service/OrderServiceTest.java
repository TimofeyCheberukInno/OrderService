package com.app.impl.unit.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.app.impl.domain.OrderStatus;
import com.app.impl.dto.order.OrderRequestDto;
import com.app.impl.dto.order.OrderResponseDto;
import com.app.impl.dto.order.OrderUpdateRequestDto;
import com.app.impl.dto.orderItem.OrderItemRequestDto;
import com.app.impl.dto.user.UserResponseDto;
import com.app.impl.entity.Item;
import com.app.impl.entity.Order;
import com.app.impl.exception.NoSuchOrderException;
import com.app.impl.mapper.OrderMapper;
import com.app.impl.mapper.OrderItemMapper;
import com.app.impl.repository.OrderRepository;
import com.app.impl.service.ItemService;
import com.app.impl.service.OrderService;
import com.app.impl.service.UserService;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    @Mock 
    private OrderRepository orderRepository;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private OrderItemMapper orderItemMapper;

    @Mock
    private ItemService itemService;

    @Mock
    private UserService userService;

    @InjectMocks
    private OrderService orderService;

    @Nested
    @DisplayName("Tests for create(OrderRequestDto orderRequestDto)")
    class createOrderTests {
        private OrderItemRequestDto orderItemRequestDto1 = new OrderItemRequestDto(1L, 2);
        private OrderItemRequestDto orderItemRequestDto2 = new OrderItemRequestDto(2L, 1);
        private List<OrderItemRequestDto> orderItems = List.of(orderItemRequestDto1, orderItemRequestDto2);
        private OrderRequestDto orderRequestDto = new OrderRequestDto("test@example.com", orderItems);

        private Order preSavedOrder = new Order(
                null,
                "test@example.com",
                OrderStatus.IN_PROCESS,
                LocalDateTime.now(),
                new ArrayList<>()
        );
        private Order postSavedOrder = new Order(
                1L,
                "test@example.com",
                OrderStatus.IN_PROCESS,
                LocalDateTime.now(),
                new ArrayList<>()
        );
        private Item item1 = new Item(1L, "item1", BigDecimal.valueOf(100.0));
        private Item item2 = new Item(2L, "item2", BigDecimal.valueOf(200.0));
        private UserResponseDto userResponseDto = new UserResponseDto(1L, "John", "Doe", LocalDate.of(1990, 1, 1), "test@example.com");
        private OrderResponseDto orderResponseDto = new OrderResponseDto(
                1L,
                OrderStatus.IN_PROCESS,
                LocalDateTime.now(),
                new ArrayList<>(),
                userResponseDto
        );

        @Test
        @DisplayName("returns OrderResponseDto if order was successfully created")
        void shouldSaveAndReturnOrder() {

            Mockito.when(orderMapper.toEntity(orderRequestDto))
                    .thenReturn(preSavedOrder);
            Mockito.when(itemService.getAllByIds(List.of(1L, 2L)))
                    .thenReturn(List.of());
            Mockito.when(itemService.getListOfItemEntitiesById(List.of(1L, 2L)))
                    .thenReturn(List.of(item1, item2));
            Mockito.when(orderRepository.save(Mockito.any(Order.class)))
                    .thenReturn(postSavedOrder);
            Mockito.when(orderMapper.toResponse(postSavedOrder))
                    .thenReturn(orderResponseDto);
            Mockito.when(userService.getUserByEmail("test@example.com"))
                    .thenReturn(userResponseDto);

            
            OrderResponseDto actualValue = orderService.create(orderRequestDto);

            
            assertThat(actualValue).isEqualTo(orderResponseDto);

            Mockito.verify(orderMapper, Mockito.times(1))
                    .toEntity(orderRequestDto);
            Mockito.verify(itemService, Mockito.times(1))
                    .getAllByIds(List.of(1L, 2L));
            Mockito.verify(itemService, Mockito.times(1))
                    .getListOfItemEntitiesById(List.of(1L, 2L));
            Mockito.verify(orderRepository, Mockito.times(1))
                    .save(Mockito.any(Order.class));
            Mockito.verify(orderMapper, Mockito.times(1))
                    .toResponse(postSavedOrder);
            Mockito.verify(orderItemMapper, Mockito.never())
                    .toDto(Mockito.any());
            Mockito.verify(userService, Mockito.times(1))
                    .getUserByEmail("test@example.com");
        }
    }

    @Nested
    @DisplayName("Tests for update(OrderUpdateRequestDto orderUpdateRequestDto)")
    class updateOrderTests {
        private OrderUpdateRequestDto orderUpdateRequestDto = new OrderUpdateRequestDto(
                1L,
                OrderStatus.COMPLETED
        );
        private Order savedOrder = new Order(
                1L,
                "test@example.com",
                OrderStatus.IN_PROCESS,
                LocalDateTime.now(),
                new ArrayList<>()
        );
        private Order updatedOrder = new Order(
                1L,
                "test@example.com",
                OrderStatus.COMPLETED,
                LocalDateTime.now(),
                new ArrayList<>()
        );
        private UserResponseDto userResponseDto = new UserResponseDto(1L, "John", "Doe", LocalDate.of(1990, 1, 1), "test@example.com");
        private OrderResponseDto orderResponseDto = new OrderResponseDto(
                1L,
                OrderStatus.COMPLETED,
                LocalDateTime.now(),
                new ArrayList<>(),
                userResponseDto
        );

        @Test
        @DisplayName("updates order")
        void shouldUpdateOrder() {

            Mockito.when(orderRepository.findById(1L))
                    .thenReturn(Optional.of(savedOrder));
            Mockito.when(orderRepository.save(Mockito.any(Order.class)))
                    .thenReturn(updatedOrder);
            Mockito.when(orderMapper.toResponse(updatedOrder))
                    .thenReturn(orderResponseDto);
            Mockito.when(userService.getUserByEmail("test@example.com"))
                    .thenReturn(userResponseDto);

            
            OrderResponseDto actualValue = orderService.update(orderUpdateRequestDto);

            
            assertThat(actualValue).isEqualTo(orderResponseDto);

            Mockito.verify(orderRepository, Mockito.times(1))
                    .findById(1L);
            Mockito.verify(orderRepository, Mockito.times(1))
                    .save(Mockito.any(Order.class));
            Mockito.verify(orderMapper, Mockito.times(1))
                    .toResponse(updatedOrder);
            Mockito.verify(orderItemMapper, Mockito.never())
                    .toDto(Mockito.any());
            Mockito.verify(userService, Mockito.times(1))
                    .getUserByEmail("test@example.com");
        }

        @Test
        @DisplayName("throws NoSuchOrderException")
        void shouldThrowNoSuchOrderException() {
            Mockito.when(orderRepository.findById(1L))
                    .thenReturn(Optional.empty());

            assertThatExceptionOfType(NoSuchOrderException.class)
                    .isThrownBy(() -> orderService.update(orderUpdateRequestDto));

            Mockito.verify(orderRepository, Mockito.times(1))
                    .findById(1L);
            Mockito.verify(orderRepository, Mockito.never())
                    .save(Mockito.any());
            Mockito.verify(orderMapper, Mockito.never())
                    .toResponse(Mockito.any());
            Mockito.verify(orderItemMapper, Mockito.never())
                    .toDto(Mockito.any());
        }
    }

    @Nested
    @DisplayName("Tests for deleteById(Long id)")
    class deleteOrderTests {
        @Test
        @DisplayName("successfully deletes order")
        void shouldDeleteOrder() {
            orderService.deleteById(1L);

            Mockito.verify(orderRepository, Mockito.times(1))
                    .deleteById(1L);
        }
    }

    @Nested
    @DisplayName("Tests for getById(Long id)")
    class getOrderByIdTests {
        private Order order = new Order(
                1L,
                "test@example.com",
                OrderStatus.IN_PROCESS,
                LocalDateTime.now(),
                new ArrayList<>()
        );
        private UserResponseDto userResponseDto = new UserResponseDto(1L, "John", "Doe", LocalDate.of(1990, 1, 1), "test@example.com");
        private OrderResponseDto orderResponseDto = new OrderResponseDto(
                1L,
                OrderStatus.IN_PROCESS,
                LocalDateTime.now(),
                new ArrayList<>(),
                userResponseDto
        );

        @Test
        @DisplayName("returns order by id")
        void shouldReturnOrderById() {

            Mockito.when(orderRepository.findById(1L))
                    .thenReturn(Optional.of(order));
            Mockito.when(orderMapper.toResponse(order))
                    .thenReturn(orderResponseDto);
            Mockito.when(userService.getUserByEmail("test@example.com"))
                    .thenReturn(userResponseDto);

            
            OrderResponseDto actualValue = orderService.getById(1L);

            
            assertThat(actualValue).isEqualTo(orderResponseDto);

            Mockito.verify(orderRepository, Mockito.times(1))
                    .findById(1L);
            Mockito.verify(orderMapper, Mockito.times(1))
                    .toResponse(order);
            Mockito.verify(orderItemMapper, Mockito.never())
                    .toDto(Mockito.any());
            Mockito.verify(userService, Mockito.times(1))
                    .getUserByEmail("test@example.com");
        }

        @Test
        @DisplayName("throws NoSuchOrderException while searching by id")
        void shouldThrowNoSuchOrderExceptionWhileFindingById() {
            Mockito.when(orderRepository.findById(1L))
                    .thenReturn(Optional.empty());

            assertThatExceptionOfType(NoSuchOrderException.class)
                    .isThrownBy(() -> orderService.getById(1L));

            Mockito.verify(orderRepository, Mockito.times(1))
                    .findById(1L);
            Mockito.verify(orderMapper, Mockito.never())
                    .toResponse(Mockito.any());
            Mockito.verify(orderItemMapper, Mockito.never())
                    .toDto(Mockito.any());
        }
    }

    @Nested
    @DisplayName("Tests for getAllByIds(List<Long> ids)")
    class getAllByIdsTests {
        private List<Order> orders = new ArrayList<>(
                List.of(
                        new Order(1L, "test1@example.com", OrderStatus.IN_PROCESS, LocalDateTime.now(), new ArrayList<>()),
                        new Order(2L, "test2@example.com", OrderStatus.COMPLETED, LocalDateTime.now(), new ArrayList<>())
                )
        );

        @Test
        @DisplayName("returns list of orders by ids")
        void shouldReturnListOfOrdersByIds() {
            Mockito.when(orderRepository.findAllById(List.of(1L, 2L)))
                    .thenReturn(orders);
            Mockito.when(orderMapper.toResponse(orders.get(0)))
                    .thenReturn(createTestOrderResponseDto(1L, OrderStatus.IN_PROCESS));
            Mockito.when(orderMapper.toResponse(orders.get(1)))
                    .thenReturn(createTestOrderResponseDto(2L, OrderStatus.COMPLETED));
            Mockito.when(userService.getUserByEmail("test1@example.com"))
                    .thenReturn(new UserResponseDto(1L, "John", "Doe", LocalDate.of(1990, 1, 1), "test1@example.com"));
            Mockito.when(userService.getUserByEmail("test2@example.com"))
                    .thenReturn(new UserResponseDto(2L, "Jane", "Smith", LocalDate.of(1995, 5, 15), "test2@example.com"));

            List<OrderResponseDto> actualValues = orderService.getAllByIds(List.of(1L, 2L));
            Assertions.assertThat(actualValues).hasSize(2);

            Mockito.verify(orderRepository, Mockito.times(1))
                    .findAllById(List.of(1L, 2L));
            Mockito.verify(orderMapper, Mockito.times(1))
                    .toResponse(orders.get(0));
            Mockito.verify(orderMapper, Mockito.times(1))
                    .toResponse(orders.get(1));
            Mockito.verify(orderItemMapper, Mockito.never())
                    .toDto(Mockito.any());
            Mockito.verify(userService, Mockito.times(1))
                    .getUserByEmail("test1@example.com");
            Mockito.verify(userService, Mockito.times(1))
                    .getUserByEmail("test2@example.com");
        }

        @Test
        @DisplayName("throws NoSuchOrderException when not all orders found")
        void shouldThrowNoSuchOrderExceptionWhenNotAllFound() {
            List<Order> partialOrders = List.of(
                    new Order(1L, "test1@example.com", OrderStatus.IN_PROCESS, LocalDateTime.now(), new ArrayList<>())
            );
            
            Mockito.when(orderRepository.findAllById(List.of(1L, 2L)))
                    .thenReturn(partialOrders);

            assertThatExceptionOfType(NoSuchOrderException.class)
                    .isThrownBy(() -> orderService.getAllByIds(List.of(1L, 2L)));

            Mockito.verify(orderRepository, Mockito.times(1))
                    .findAllById(List.of(1L, 2L));
            Mockito.verify(orderItemMapper, Mockito.never())
                    .toDto(Mockito.any());
            Mockito.verify(userService, Mockito.never())
                    .getUserByEmail(Mockito.any());
        }
    }

    @Nested
    @DisplayName("Tests for getAllByStatus(OrderStatus orderStatus)")
    class getAllByStatusTests {
        private List<Order> orders = new ArrayList<>(
                List.of(
                        new Order(1L, "test1@example.com", OrderStatus.IN_PROCESS, LocalDateTime.now(), new ArrayList<>()),
                        new Order(2L, "test2@example.com", OrderStatus.IN_PROCESS, LocalDateTime.now(), new ArrayList<>())
                )
        );

        @Test
        @DisplayName("returns list of orders by status")
        void shouldReturnListOfOrdersByStatus() {
            Mockito.when(orderRepository.findAllByStatus(OrderStatus.IN_PROCESS))
                    .thenReturn(orders);
            Mockito.when(orderMapper.toResponse(orders.get(0)))
                    .thenReturn(createTestOrderResponseDto(1L, OrderStatus.IN_PROCESS));
            Mockito.when(orderMapper.toResponse(orders.get(1)))
                    .thenReturn(createTestOrderResponseDto(2L, OrderStatus.IN_PROCESS));
            Mockito.when(userService.getUserByEmail("test1@example.com"))
                    .thenReturn(new UserResponseDto(1L, "John", "Doe", LocalDate.of(1990, 1, 1), "test1@example.com"));
            Mockito.when(userService.getUserByEmail("test2@example.com"))
                    .thenReturn(new UserResponseDto(2L, "Jane", "Smith", LocalDate.of(1995, 5, 15), "test2@example.com"));

            List<OrderResponseDto> actualValues = orderService.getAllByStatus(OrderStatus.IN_PROCESS);
            Assertions.assertThat(actualValues).hasSize(2);

            Mockito.verify(orderRepository, Mockito.times(1))
                    .findAllByStatus(OrderStatus.IN_PROCESS);
            Mockito.verify(orderMapper, Mockito.times(1))
                    .toResponse(orders.get(0));
            Mockito.verify(orderMapper, Mockito.times(1))
                    .toResponse(orders.get(1));
            Mockito.verify(orderItemMapper, Mockito.never())
                    .toDto(Mockito.any());
            Mockito.verify(userService, Mockito.times(1))
                    .getUserByEmail("test1@example.com");
            Mockito.verify(userService, Mockito.times(1))
                    .getUserByEmail("test2@example.com");
        }

        @Test
        @DisplayName("returns empty list when no orders found")
        void shouldReturnEmptyListWhenNoOrdersFound() {
            Mockito.when(orderRepository.findAllByStatus(OrderStatus.CANCELLED))
                    .thenReturn(List.of());

            List<OrderResponseDto> actualValues = orderService.getAllByStatus(OrderStatus.CANCELLED);
            Assertions.assertThat(actualValues).isEmpty();

            Mockito.verify(orderRepository, Mockito.times(1))
                    .findAllByStatus(OrderStatus.CANCELLED);
            Mockito.verify(orderItemMapper, Mockito.never())
                    .toDto(Mockito.any());
            Mockito.verify(userService, Mockito.never())
                    .getUserByEmail(Mockito.any());
        }
    }

    @Nested
    @DisplayName("Tests for getAll()")
    class findAllTests {
        private List<Order> orders = new ArrayList<>(
                List.of(
                        new Order(1L, "test1@example.com", OrderStatus.IN_PROCESS, LocalDateTime.now(), new ArrayList<>()),
                        new Order(2L, "test2@example.com", OrderStatus.COMPLETED, LocalDateTime.now(), new ArrayList<>())
                )
        );

        @Test
        @DisplayName("return not empty list")
        void shouldReturnNotEmptyList() {
            Mockito.when(orderRepository.findAll())
                    .thenReturn(orders);
            Mockito.when(orderMapper.toResponse(orders.get(0)))
                    .thenReturn(createTestOrderResponseDto(1L, OrderStatus.IN_PROCESS));
            Mockito.when(orderMapper.toResponse(orders.get(1)))
                    .thenReturn(createTestOrderResponseDto(2L, OrderStatus.COMPLETED));
            Mockito.when(userService.getUserByEmail("test1@example.com"))
                    .thenReturn(new UserResponseDto(1L, "John", "Doe", LocalDate.of(1990, 1, 1), "test1@example.com"));
            Mockito.when(userService.getUserByEmail("test2@example.com"))
                    .thenReturn(new UserResponseDto(2L, "Jane", "Smith", LocalDate.of(1995, 5, 15), "test2@example.com"));


            List<OrderResponseDto> actualValues = orderService.getAll();
            Assertions.assertThat(actualValues).hasSize(2);

            Mockito.verify(orderRepository, Mockito.times(1))
                    .findAll();
            Mockito.verify(orderMapper, Mockito.times(1))
                    .toResponse(orders.get(0));
            Mockito.verify(orderMapper, Mockito.times(1))
                    .toResponse(orders.get(1));
            Mockito.verify(orderItemMapper, Mockito.never())
                    .toDto(Mockito.any());
            Mockito.verify(userService, Mockito.times(1))
                    .getUserByEmail("test1@example.com");
            Mockito.verify(userService, Mockito.times(1))
                    .getUserByEmail("test2@example.com");
        }

        @Test
        @DisplayName("return empty list")
        void shouldReturnEmptyList() {
            Mockito.when(orderRepository.findAll())
                    .thenReturn(List.of());

            List<OrderResponseDto> actualValues = orderService.getAll();
            Assertions.assertThat(actualValues).isEmpty();

            Mockito.verify(orderRepository, Mockito.times(1))
                    .findAll();
            Mockito.verify(orderItemMapper, Mockito.never())
                    .toDto(Mockito.any());
            Mockito.verify(userService, Mockito.never())
                    .getUserByEmail(Mockito.any());
        }
    }

    // Helper methods
    private OrderResponseDto createTestOrderResponseDto(Long id, OrderStatus status) {
        return new OrderResponseDto(
                id,
                status,
                LocalDateTime.now(),
                new ArrayList<>(),
                null
        );
    }
}
