package com.app.impl.dto.orderItem;

public record OrderItemResponseDto(
    Long id,
    Long itemId,
    int quantity
) { }
