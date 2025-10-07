package com.app.impl.dto.order;

import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import com.app.impl.dto.orderItem.OrderItemRequestDto;

public record OrderRequestDto(
        @NotBlank
        @Email
        String userEmail,

        @NotEmpty
        List<OrderItemRequestDto> orderItems
) { }
