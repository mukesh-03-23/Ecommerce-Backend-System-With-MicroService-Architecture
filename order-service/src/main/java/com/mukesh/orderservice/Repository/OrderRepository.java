package com.mukesh.orderservice.Repository;

import com.mukesh.orderservice.Entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order,Long> {

    List<Order> findOrdersByEmail(String email);

    Order findOrderByOrderId(String orderId);
}
