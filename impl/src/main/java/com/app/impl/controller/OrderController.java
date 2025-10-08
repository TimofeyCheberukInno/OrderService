package com.app.impl.controller;

import java.util.List;

import com.app.impl.domain.OrderStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.impl.dto.order.OrderRequestDto;
import com.app.impl.dto.order.OrderUpdateRequestDto;
import com.app.impl.dto.order.OrderResponseDto;
import com.app.impl.service.OrderService;

@RestController
@RequestMapping("/api/orders")
@Validated
public class OrderController {
    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder(@RequestBody @Valid OrderRequestDto orderRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.create(orderRequestDto));
    }

    @PutMapping
    public ResponseEntity<OrderResponseDto> updateOrder(@RequestBody @Valid OrderUpdateRequestDto orderUpdateRequestDto) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(orderService.update(orderUpdateRequestDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDto> getOrderById(@PathVariable @Positive Long id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(orderService.getById(id));
    }

    @GetMapping(params = "ids")
    public ResponseEntity<List<OrderResponseDto>> getOrdersById(@RequestParam List<@Positive Long> ids) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(orderService.getAllByIds(ids));
    }

    @GetMapping(path = "/by-status", params = "status")
    public ResponseEntity<List<OrderResponseDto>> getOrdersByStatus(@RequestParam OrderStatus status) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(orderService.getAllByStatus(status));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponseDto>> getAllOrders() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(orderService.getAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrderById(@PathVariable @Positive Long id) {
        orderService.deleteById(id);
        return ResponseEntity.status(HttpStatus.OK)
                .build();
    }
}
