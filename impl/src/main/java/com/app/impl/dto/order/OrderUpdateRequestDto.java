package com.app.impl.dto.order;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import com.app.impl.domain.OrderStatus;

public record OrderUpdateRequestDto(
        @NotNull
        @Positive
        Long id,

        @NotNull
        OrderStatus status
) { }
