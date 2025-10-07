package com.app.impl.service;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.impl.exception.NoSuchItemException;
import com.app.impl.dto.item.ItemUpdateRequestDto;
import com.app.impl.dto.item.ItemRequestDto;
import com.app.impl.dto.item.ItemResponseDto;
import com.app.impl.entity.Item;
import com.app.impl.mapper.ItemMapper;
import com.app.impl.repository.ItemRepository;

@Service
public class ItemService {
    ItemRepository itemRepository;
    ItemMapper itemMapper;

    @Autowired
    public ItemService(
            ItemRepository itemRepository,
            ItemMapper itemMapper
    ) {
        this.itemRepository = itemRepository;
        this.itemMapper = itemMapper;
    }

    @Transactional
    public ItemResponseDto create(ItemRequestDto itemRequestDto) {
        Item item = itemMapper.toEntity(itemRequestDto);
        Item savedItem = itemRepository.save(item);
        return itemMapper.toResponse(savedItem);
    }

    @Transactional
    public ItemResponseDto update(ItemUpdateRequestDto itemUpdateRequestDto) {
        Item itemToUpdate = itemRepository.findById(itemUpdateRequestDto.id())
                .orElseThrow(() -> new NoSuchItemException(Collections.singleton(itemUpdateRequestDto.id())));

        Item updatedItem = updateItemFields(itemToUpdate, itemUpdateRequestDto);
        return itemMapper.toResponse(updatedItem);
    }

    @Transactional(readOnly = true)
    public ItemResponseDto getById(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NoSuchItemException(Collections.singleton(id)));

        return itemMapper.toResponse(item);
    }

    @Transactional(readOnly = true)
    public List<ItemResponseDto> getAllByIds(List<Long> ids) {
        List<Item> items = itemRepository.findAllById(ids);

        if(ids.size() != items.size()) {
            Set<Long> foundIds = items.stream()
                    .map(Item::getId)
                    .collect(Collectors.toSet());

            List<Long> notFoundIds = ids.stream()
                    .filter(id -> !foundIds.contains(id))
                    .toList();

            throw new NoSuchItemException(notFoundIds);
        }

        return itemMapper.toResponseList(items);
    }

    @Transactional(readOnly = true)
    public List<ItemResponseDto> getAll() {
        List<Item> items = itemRepository.findAll();
        return itemMapper.toResponseList(items);
    }

    @Transactional
    public void deleteById(Long id) {
        itemRepository.deleteById(id);
    }

    private Item updateItemFields(Item itemToUpdate, ItemUpdateRequestDto itemUpdateRequestDto) {
        itemToUpdate.setName(itemUpdateRequestDto.name());
        itemToUpdate.setPrice(itemUpdateRequestDto.price());
        return itemToUpdate;
    }
}
