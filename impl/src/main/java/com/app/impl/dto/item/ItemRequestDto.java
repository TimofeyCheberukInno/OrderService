package com.app.impl.dto.item;

import java.math.BigDecimal;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record ItemRequestDto (
        @NotBlank
        @Size(max = 50)
        String name,

        @NotNull
        @Digits(integer = 10, fraction = 3)
        @Positive
        BigDecimal price
) { }
