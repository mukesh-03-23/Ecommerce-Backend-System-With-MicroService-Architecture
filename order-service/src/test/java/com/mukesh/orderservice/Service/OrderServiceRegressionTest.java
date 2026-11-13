package com.mukesh.orderservice.Service;

import com.mukesh.orderservice.Client.CartClient;
import com.mukesh.orderservice.Client.PaymentClient;
import com.mukesh.orderservice.Client.ProductClient;
import com.mukesh.orderservice.Entity.Order;
import com.mukesh.orderservice.Entity.OrderItem;
import com.mukesh.orderservice.Entity.paymentStatus;
import com.mukesh.orderservice.Repository.OrderItemRepository;
import com.mukesh.orderservice.Repository.OrderRepository;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceRegressionTest {
    private final OrderRepository orders = mock(OrderRepository.class);
    private final ProductClient products = mock(ProductClient.class);
    private final OrderService service = new OrderService(mock(CartClient.class), orders,
            mock(PaymentClient.class), mock(OrderItemRepository.class), products);

    @Test void completedPaymentUpdatesOrderToPaid() {
        Order order = new Order();
        order.setPaymentStatus(paymentStatus.PENDING);
        when(orders.findOrderByOrderId("ORD-A")).thenReturn(order);
        service.UpdatePayment("ORD-A");
        assertEquals(paymentStatus.PAID, order.getPaymentStatus());
        verify(orders).save(order);
    }

    @Test void inventoryCallsCarryOrderIdForIdempotency() {
        Order order = new Order();
        OrderItem item = new OrderItem();
        item.setProductId(1L); item.setQuantity(2);
        order.setItems(List.of(item));
        when(orders.findOrderByOrderId("ORD-A")).thenReturn(order);
        service.updateInventory("ORD-A");
        var captor = org.mockito.ArgumentCaptor.forClass(com.mukesh.orderservice.DTO.InventoryRequest.class);
        verify(products).UpdateInventory(captor.capture());
        assertEquals("ORD-A", captor.getValue().getOrderId());
        assertEquals(2, captor.getValue().getOrderquantity());
    }
}
