package com.app.impl.controller;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.impl.dto.item.ItemRequestDto;
import com.app.impl.dto.item.ItemUpdateRequestDto;
import com.app.impl.dto.item.ItemResponseDto;
import com.app.impl.service.ItemService;

@RestController
@RequestMapping("/api/items")
@Validated
public class ItemController {
    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ResponseEntity<ItemResponseDto> createItem(@RequestBody @Valid ItemRequestDto itemRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(itemService.create(itemRequestDto));
    }

    @PutMapping
    public ResponseEntity<ItemResponseDto> updateItem(@RequestBody @Valid ItemUpdateRequestDto itemUpdateRequestDto) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(itemService.update(itemUpdateRequestDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemResponseDto> getItemById(@PathVariable @Positive Long id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(itemService.getById(id));
    }

    @GetMapping(params = "ids")
    public ResponseEntity<List<ItemResponseDto>> getAllItems(@RequestParam List<@Positive Long> ids) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(itemService.getAllByIds(ids));
    }

    @GetMapping
    public ResponseEntity<List<ItemResponseDto>> getAllItems() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(itemService.getAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable @Positive Long id) {
        itemService.deleteById(id);
        return ResponseEntity.status(HttpStatus.OK)
                .build();
    }
}
