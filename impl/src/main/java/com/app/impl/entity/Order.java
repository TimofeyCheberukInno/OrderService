package com.app.impl.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Index;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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
                        name = "idx_orders_user_email",
                        columnList = "user_email"
                )
        }
)
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "user_email", nullable = false)
    private String userEmail;

    @Column(name = "status", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.IN_PROCESS;

    @Column(name = "creation_date", nullable = false)
    @CreatedDate
    private LocalDateTime creationDate;

    // With cascading automatically saves OrderItems in DB
    @OneToMany(mappedBy = "order", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();
}
