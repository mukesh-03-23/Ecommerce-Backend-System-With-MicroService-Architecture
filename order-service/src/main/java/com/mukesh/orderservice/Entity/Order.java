package com.mukesh.orderservice.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@Table(name="orders")
public class Order {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    private String orderId;
    public Order() {
        UUID  uuid = UUID.randomUUID();
        this.orderId = "ORD"+uuid.toString().substring(0,8);
    }
    private String email;
    @OneToMany(cascade = CascadeType.ALL,mappedBy = "order",orphanRemoval = true)
    private List<OrderItem> items;
    private Double ordertotal;
    @Enumerated(EnumType.STRING)
    private paymentStatus paymentStatus;
}
