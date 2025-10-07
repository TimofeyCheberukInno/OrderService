package com.app.impl.dto.order;

import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record OrderRequestDto(
        @NotBlank
        @Email
        String userEmail,

        @NotEmpty
        List<OrderItemRequestDto> orderItems
) {
    public record OrderItemRequestDto(
            @NotNull
            @Positive
            Long itemId,

            @NotNull
            @Positive
            int quantity
    ) { }
}
