package com.app.impl.unit.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.app.impl.dto.item.ItemRequestDto;
import com.app.impl.dto.item.ItemResponseDto;
import com.app.impl.dto.item.ItemUpdateRequestDto;
import com.app.impl.entity.Item;
import com.app.impl.exception.NoSuchItemException;
import com.app.impl.mapper.ItemMapper;
import com.app.impl.repository.ItemRepository;
import com.app.impl.service.ItemService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {
    @Mock 
    private ItemRepository itemRepository;

    @Mock
    private ItemMapper itemMapper;

    @InjectMocks
    private ItemService itemService;

    @Nested
    @DisplayName("Tests for create(ItemRequestDto itemRequestDto)")
    class createItemTests {
        private ItemRequestDto itemRequestDto = new ItemRequestDto(
                "test_item",
                BigDecimal.valueOf(144.44)
        );
        private Item preSavedItem = new Item(
                null,
                "test_item",
                BigDecimal.valueOf(144.44)
        );
        private Item postSavedItem = new Item(
                1L,
                "test_item",
                BigDecimal.valueOf(144.44)
        );
        private ItemResponseDto itemResponseDto = new ItemResponseDto(
                1L,
                "test_item",
                BigDecimal.valueOf(144.44)
        );

        @Test
        @DisplayName("returns ItemResponseDto if item was successfully created")
        void shouldSaveAndReturnUser() {
            Mockito.when(itemMapper.toEntity(itemRequestDto))
                    .thenReturn(preSavedItem);
            Mockito.when(itemRepository.save(preSavedItem))
                    .thenReturn(postSavedItem);
            Mockito.when(itemMapper.toResponse(postSavedItem))
                    .thenReturn(itemResponseDto);

            ItemResponseDto actualValue = itemService.create(itemRequestDto);

            assertThat(actualValue).isEqualTo(itemResponseDto);

            Mockito.verify(itemMapper, Mockito.times(1))
                    .toEntity(itemRequestDto);
            Mockito.verify(itemRepository, Mockito.times(1))
                    .save(preSavedItem);
            Mockito.verify(itemMapper, Mockito.times(1))
                    .toResponse(postSavedItem);
        }
    }

    @Nested
    @DisplayName("Tests for update(ItemUpdateRequestDto itemUpdateRequestDto)")
    class updateItemTests {
        private ItemUpdateRequestDto itemUpdateRequestDto = new ItemUpdateRequestDto(
                1L,
                "test_update_item",
                BigDecimal.valueOf(135.2)
        );
        private Item savedItem = new Item(
                1L,
                "test_item",
                BigDecimal.valueOf(144.44)
        );

        private Item updatedItem = new Item(
                1L,
                "test_update_item",
                BigDecimal.valueOf(135.2)
        );

        private ItemResponseDto itemResponseDto = new ItemResponseDto(
                1L,
                "test_update_item",
                BigDecimal.valueOf(135.2)
        );

        @Test
        @DisplayName("updates item")
        void shouldUpdateItem() {
            Mockito.when(itemRepository.findById(itemUpdateRequestDto.id()))
                    .thenReturn(Optional.of(savedItem));
            Mockito.when(itemMapper.toResponse(updatedItem))
                    .thenReturn(itemResponseDto);

            ItemResponseDto actualValue = itemService.update(itemUpdateRequestDto);

            assertThat(actualValue).isEqualTo(itemResponseDto);

            Mockito.verify(itemRepository, Mockito.times(1))
                    .findById(itemUpdateRequestDto.id());
            Mockito.verify(itemMapper, Mockito.times(1))
                    .toResponse(savedItem);
        }

        @Test
        @DisplayName("throws NoSuchItemException")
        void shouldThrowNoSuchItemException() {
            Mockito.when(itemRepository.findById(itemUpdateRequestDto.id()))
                    .thenReturn(Optional.empty());

            assertThatExceptionOfType(NoSuchItemException.class)
                    .isThrownBy(() -> itemService.update(itemUpdateRequestDto));

            Mockito.verify(itemRepository, Mockito.times(1))
                    .findById(itemUpdateRequestDto.id());
            Mockito.verify(itemMapper, Mockito.never())
                    .toResponse(Mockito.any());
        }
    }

    @Nested
    @DisplayName("Tests for deleteById(Long id)")
    class deleteItemTests {
        @Test
        @DisplayName("successfully deletes item")
        void shouldDeleteItem() {
            itemService.deleteById(1L);

            Mockito.verify(itemRepository, Mockito.times(1))
                    .deleteById(1L);
        }
    }

    @Nested
    @DisplayName("Tests for getById(Long id)")
    class getItemByIdTests {
        private Item item = new Item(
                1L,
                "test_item",
                BigDecimal.valueOf(144.44)
        );
        private ItemResponseDto itemResponseDto = new ItemResponseDto(
                1L,
                "test_item",
                BigDecimal.valueOf(144.44)
        );

        @Test
        @DisplayName("returns user by id")
        void shouldReturnUserByIdFromCache() {
            Mockito.when(itemRepository.findById(1L))
                    .thenReturn(Optional.of(item));
            Mockito.when(itemMapper.toResponse(item))
                    .thenReturn(itemResponseDto);

            ItemResponseDto actualValue = itemService.getById(1L);

            assertThat(actualValue).isEqualTo(itemResponseDto);

            Mockito.verify(itemRepository, Mockito.times(1))
                    .findById(1L);
            Mockito.verify(itemMapper, Mockito.times(1))
                    .toResponse(item);
        }

        @Test
        @DisplayName("returns NoSuchItemException while searching by id")
        void shouldThrowNoSuchItemExceptionWhileFindingById() {
            Mockito.when(itemRepository.findById(1L))
                    .thenReturn(Optional.empty());

            assertThatExceptionOfType(NoSuchItemException.class)
                    .isThrownBy(() -> itemService.getById(1L));

            Mockito.verify(itemRepository, Mockito.times(1))
                    .findById(1L);
            Mockito.verify(itemMapper, Mockito.never())
                    .toResponse(Mockito.any());
        }
    }

    @Nested
    @DisplayName("Tests for getAllByIds(List<Long> ids)")
    class getAllByIdsByTests {
        List<Item> items = new ArrayList<>(
                List.of(
                        new Item(
                                1L,
                                "test_item_1",
                                BigDecimal.valueOf(144.44)
                        ),
                        new Item(
                                2L,
                                "test_item_2",
                                BigDecimal.valueOf(244.32)
                        )
                )
        );

        List<ItemResponseDto> mappedItems = new ArrayList<>(
                List.of(
                        new ItemResponseDto(
                                1L,
                                "test_item_1",
                                BigDecimal.valueOf(144.44)
                        ),
                        new ItemResponseDto(
                                2L,
                                "test_item_2",
                                BigDecimal.valueOf(244.32)
                        )
                )
        );

        @Test
        @DisplayName("returns list of items by ids")
        void shouldReturnListOfUsersByIds() {
            Mockito.when(itemRepository.findAllById(List.of(1L, 2L)))
                    .thenReturn(items);
            Mockito.when(itemMapper.toResponseList(items))
                    .thenReturn(mappedItems);

            List<ItemResponseDto> actualValues = itemService.getAllByIds(List.of(1L, 2L));

            Assertions.assertThat(actualValues).containsExactlyInAnyOrderElementsOf(mappedItems);

            Mockito.verify(itemRepository, Mockito.times(1))
                    .findAllById(List.of(1L, 2L));
            Mockito.verify(itemMapper, Mockito.times(1))
                    .toResponseList(items);
        }

        @Test
        @DisplayName("returns NoSuchItemException")
        void shouldReturnNoSuchItemException() {
            Mockito.when(itemRepository.findAllById(List.of(1L, 2L)))
                    .thenReturn(List.of());

            assertThatExceptionOfType(NoSuchItemException.class)
                    .isThrownBy(() -> itemService.getAllByIds(List.of(1L, 2L)));

            Mockito.verify(itemRepository, Mockito.times(1))
                    .findAllById(List.of(1L, 2L));
            Mockito.verify(itemMapper, Mockito.never())
                    .toResponseList(Mockito.any());
        }
    }

    @Nested
    @DisplayName("Tests for getAll()")
    class findAllTests {
        List<Item> items = new ArrayList<>(
                List.of(
                        new Item(
                                1L,
                                "test_item_1",
                                BigDecimal.valueOf(144.44)
                        ),
                        new Item(
                                2L,
                                "test_item_2",
                                BigDecimal.valueOf(244.32)
                        )
                )
        );

        List<ItemResponseDto> mappedItems = new ArrayList<>(
                List.of(
                        new ItemResponseDto(
                                1L,
                                "test_item_1",
                                BigDecimal.valueOf(144.44)
                        ),
                        new ItemResponseDto(
                                2L,
                                "test_item_2",
                                BigDecimal.valueOf(244.32)
                        )
                )
        );

        @Test
        @DisplayName("return not empty list")
        void shouldReturnNotEmptyList() {
            Mockito.when(itemRepository.findAll())
                    .thenReturn(items);
            Mockito.when(itemMapper.toResponseList(items))
                    .thenReturn(mappedItems);

            List<ItemResponseDto> actualValues = itemService.getAll();

            Assertions.assertThat(actualValues).containsExactlyInAnyOrderElementsOf(mappedItems);

            Mockito.verify(itemRepository, Mockito.times(1))
                    .findAll();
            Mockito.verify(itemMapper, Mockito.times(1))
                    .toResponseList(items);
        }

        @Test
        @DisplayName("return empty list")
        void shouldReturnEmptyList() {
            Mockito.when(itemRepository.findAll())
                    .thenReturn(List.of());
            Mockito.when(itemMapper.toResponseList(List.of()))
                    .thenReturn(List.of());

            List<ItemResponseDto> actualValues = itemService.getAll();

            Assertions.assertThat(actualValues).containsExactlyInAnyOrderElementsOf(List.of());

            Mockito.verify(itemRepository, Mockito.times(1))
                    .findAll();
            Mockito.verify(itemMapper, Mockito.times(1))
                    .toResponseList(List.of());
        }
    }
}