package com.app.impl.dto.order;

import java.util.List;
import java.time.LocalDateTime;

import com.app.impl.domain.OrderStatus;
import com.app.impl.dto.orderItem.OrderItemDto;

public record OrderResponseDto(
    Long id,
    Long userId,
    OrderStatus status,
    LocalDateTime creationDate,
    List<OrderItemDto> orderItems
) { }
