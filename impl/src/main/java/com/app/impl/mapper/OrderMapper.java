package com.app.impl.mapper;

import java.util.List;

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
        order.setUserEmail(dto.userEmail());
        return order;
    }

    OrderResponseDto toResponse(Order order);

    List<OrderResponseDto> toResponseList(List<Order> orders);
}
