package com.app.impl.dto.order;

import java.util.List;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.app.impl.domain.OrderStatus;
import com.app.impl.dto.user.UserResponseDto;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDto {
    private Long id;
    private OrderStatus status;
    private LocalDateTime creationDate;
    private List<OrderItemResponseDto> orderItems;
    private UserResponseDto userDto;

    public record OrderItemResponseDto(
            Long id,
            Long itemId,
            int quantity
    ) { }
}
