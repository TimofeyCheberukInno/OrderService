package com.app.impl.dto.order;

import java.util.List;

import com.app.impl.dto.orderItem.OrderItemDto;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record OrderRequestDto(
        @NotNull
        @Positive
        Long userId,

        @NotEmpty
        List<OrderItemDto> orderItems
) { }
