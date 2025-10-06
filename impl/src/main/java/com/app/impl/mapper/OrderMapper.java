package com.app.impl.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import com.app.impl.dto.order.OrderRequestDto;
import com.app.impl.dto.order.OrderResponseDto;
import com.app.impl.entity.Order;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {OrderItemMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface OrderMapper {
    default Order toEntity(OrderRequestDto dto) {
        Order order = new Order();
        order.setUserId(dto.userId());
        return order;
    }

    OrderResponseDto toResponse(Order order);
}
