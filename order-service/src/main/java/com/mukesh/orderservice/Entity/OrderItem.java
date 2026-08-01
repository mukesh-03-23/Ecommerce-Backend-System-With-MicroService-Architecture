package com.mukesh.orderservice.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long productId;
    @ManyToOne
    @JoinColumn(name="orderId")
    private Order order;
    private Integer quantity;
    private Double subtotal;
}
