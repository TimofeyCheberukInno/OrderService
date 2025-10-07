package com.app.impl.dto.order;

import java.util.List;
import java.time.LocalDateTime;

import com.app.impl.domain.OrderStatus;
import com.app.impl.dto.user.UserResponseDto;

public record OrderResponseDto(
        Long id,
        OrderStatus status,
        LocalDateTime creationDate,
        List<OrderItemResponseDto> orderItems,
        UserResponseDto userDto
) {
    public record OrderItemResponseDto(
            Long id,
            Long itemId,
            int quantity
    ) { }
}
