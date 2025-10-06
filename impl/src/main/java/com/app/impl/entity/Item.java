package com.app.impl.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(
        name = "items",
        indexes = {
                @Index(
                        name = "idx_items_name",
                        columnList = "name"
                )
        },
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_items_name_price",
                        columnNames = {"name" , "price"}
                )
        }
)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "price", precision = 10, scale = 3, nullable = false)
    private BigDecimal price;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || o.getClass() != this.getClass())
            return false;
        Item item = (Item) o;

        if(this.id == null || item.id == null)
            return false;

        return this.id.equals(item.id);
    }

    @Override
    public int hashCode() {
        return id == null ? 222 : this.id.hashCode();
    }
}
