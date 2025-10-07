package com.app.impl.dto.orderItem;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record OrderItemRequestDto(
        @NotNull
        @Positive
        Long itemId,

        @NotNull
        @Positive
        int quantity
) { }