package com.mukesh.orderservice.Repository;

import com.mukesh.orderservice.Entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface  OrderItemRepository extends JpaRepository<OrderItem,Long> {

}
