package com.app.impl.dto.item;

import java.math.BigDecimal;

public record ItemResponseDto(
        Long id,
        String name,
        BigDecimal price
) { }
