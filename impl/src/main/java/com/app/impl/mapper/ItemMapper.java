package com.app.impl.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import com.app.impl.dto.item.ItemResponseDto;
import com.app.impl.dto.item.ItemRequestDto;
import com.app.impl.entity.Item;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ItemMapper {
    Item toEntity(ItemRequestDto itemRequestDto);

    ItemResponseDto toResponse(Item item);

    List<ItemResponseDto> toResponseList(List<Item> items);
}
