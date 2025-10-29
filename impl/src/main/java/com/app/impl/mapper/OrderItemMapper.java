package com.app.impl.mapper;

import com.app.impl.dto.order.OrderResponseDto.OrderItemResponseDto;
import com.app.impl.entity.OrderItem;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface OrderItemMapper {
    @Mapping(source = "item.id", target = "itemId")
    OrderItemResponseDto toDto(OrderItem orderItem);
}
