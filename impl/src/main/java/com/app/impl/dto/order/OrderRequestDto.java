package com.app.impl.dto.order;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import com.app.impl.dto.orderItem.OrderItemRequestDto;

public record OrderRequestDto(
        @NotNull
        @Positive
        Long userId,

        @NotEmpty
        List<OrderItemRequestDto> orderItems
) { }
