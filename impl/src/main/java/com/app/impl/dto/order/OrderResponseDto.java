package com.app.impl.dto.order;

import java.util.List;
import java.time.LocalDateTime;

import com.app.impl.domain.OrderStatus;
import com.app.impl.dto.orderItem.OrderItemResponseDto;

public record OrderResponseDto(
        Long id,
        String userEmail,
        OrderStatus status,
        LocalDateTime creationDate,
        List<OrderItemResponseDto> orderItems
) { }
