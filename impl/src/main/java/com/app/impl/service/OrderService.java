package com.app.impl.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.impl.domain.OrderStatus;
import com.app.impl.dto.order.OrderUpdateRequestDto;
import com.app.impl.entity.Item;
import com.app.impl.entity.Order;
import com.app.impl.entity.OrderItem;
import com.app.impl.exception.NoSuchOrderException;
import com.app.impl.dto.order.OrderRequestDto;
import com.app.impl.dto.order.OrderResponseDto;
import com.app.impl.dto.orderItem.OrderItemRequestDto;
import com.app.impl.mapper.OrderMapper;
import com.app.impl.repository.OrderRepository;

// FIXME: add endpoint for batch user uploading by emails
//  and change it in methods in this class
@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final ItemService itemService;
    private final UserService userService;

    @Autowired
    public OrderService(
            OrderRepository orderRepository,
            OrderMapper orderMapper,
            ItemService itemService,
            UserService userService
    ) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.itemService = itemService;
        this.userService = userService;
    }

    @Transactional
    public OrderResponseDto create(OrderRequestDto orderRequestDto) {
        validateOrderItems(orderRequestDto.orderItems());

        Order order = orderMapper.toEntity(orderRequestDto);
        Order orderWithItems = addOrderItemsToOrder(order, orderRequestDto.orderItems());
        Order savedOrder = orderRepository.save(orderWithItems);
        OrderResponseDto response = orderMapper.toResponse(savedOrder);
        response.setUserDto(userService.getUserByEmail(orderRequestDto.userEmail()));
        return response;
    }

    @Transactional
    public OrderResponseDto update(OrderUpdateRequestDto orderUpdateRequestDto) {
        Order orderToUpdate = orderRepository.findById(orderUpdateRequestDto.id())
                .orElseThrow(() -> new NoSuchOrderException(Collections.singleton(orderUpdateRequestDto.id())));

        Order updatedOrder = updateOrderFields(orderToUpdate, orderUpdateRequestDto);
        OrderResponseDto response = orderMapper.toResponse(orderRepository.save(updatedOrder));
        response.setUserDto(userService.getUserByEmail(updatedOrder.getUserEmail()));
        return response;
    }

    @Transactional(readOnly = true)
    public OrderResponseDto findById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NoSuchOrderException(Collections.singleton(id)));
        OrderResponseDto response =  orderMapper.toResponse(order);
        response.setUserDto(userService.getUserByEmail(order.getUserEmail()));
        return response;
    }

    @Transactional(readOnly = true)
    public List<OrderResponseDto> findAllByIds(List<Long> ids) {
        List<Order> orders = orderRepository.findAllById(ids);

        if(orders.size() != ids.size()){
            Set<Long> foundIds = orders.stream()
                    .map(Order::getId)
                    .collect(Collectors.toSet());

            List<Long> notFoundIds = ids.stream()
                    .filter(id -> !foundIds.contains(id))
                    .toList();

            throw new NoSuchOrderException(notFoundIds);
        }

        return toResponseList(orders);
    }

    @Transactional(readOnly = true)
    public List<OrderResponseDto> findAllByStatus(OrderStatus orderStatus) {
        List<Order> orders = orderRepository.findAllByStatus(orderStatus);
        return toResponseList(orders);
    }

    @Transactional(readOnly = true)
    public List<OrderResponseDto> findAll() {
        List<Order> orders = orderRepository.findAll();
        return toResponseList(orders);
    }

    @Transactional
    public void deleteById(Long id) {
        orderRepository.deleteById(id);
    }

    private void validateOrderItems(List<OrderItemRequestDto> orderItems) {
        List<Long> ids = orderItems.stream()
                .map(OrderItemRequestDto::itemId)
                .toList();
        itemService.getAllByIds(ids);
    }

    private Order addOrderItemsToOrder(Order order, List<OrderItemRequestDto> orderItems) {
        // merging duplicates
        Map<Long, Integer> mergedOrderItems = orderItems.stream()
                .collect(Collectors.toMap(
                        OrderItemRequestDto::itemId,
                        OrderItemRequestDto::quantity,
                        Integer::sum
                ));

        // getting correspond items
        List<Long> ids = new ArrayList<>(mergedOrderItems.keySet());
        Map<Long, Item> items = itemService.getListOfItemEntitiesById(ids).stream()
                .collect(Collectors.toMap(Item::getId, item -> item));

        mergedOrderItems.forEach((key, value) -> {
            OrderItem orderItem =
                    new OrderItem(null, order, items.get(key), value);
            order.getOrderItems().add(orderItem);
        });

        return order;
    }

    private Order updateOrderFields(Order orderToUpdate, OrderUpdateRequestDto orderUpdateRequestDto) {
        orderToUpdate.setStatus(orderUpdateRequestDto.status());
        return orderToUpdate;
    }

    private List<OrderResponseDto> toResponseList(List<Order> orders) {
        return orders.stream()
                .map(order -> {
                    OrderResponseDto responseDto = orderMapper.toResponse(order);
                    responseDto.setUserDto(userService.getUserByEmail(order.getUserEmail()));
                    return responseDto;
                })
                .toList();
    }
}