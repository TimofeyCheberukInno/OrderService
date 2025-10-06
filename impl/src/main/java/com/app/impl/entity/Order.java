package com.app.impl.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Index;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import com.app.impl.domain.OrderStatus;

@Entity
@Table(
        name = "orders",
        indexes = {
                @Index(
                        name = "idx_orders_user_id",
                        columnList = "user_id"
                )
        }
)
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long user_id;

    @Column(name = "status", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(name = "creation_date", nullable = false)
    private LocalDateTime creation_date;

    @OneToMany(mappedBy = "order", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<OrderItem> order_items;
}
