package com.app.impl.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.impl.entity.Order;
import com.app.impl.domain.OrderStatus;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByStatusIn(List<OrderStatus> status);
}
