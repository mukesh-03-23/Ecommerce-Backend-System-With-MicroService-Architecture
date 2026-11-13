    package com.mukesh.orderservice.Service;
    import com.mukesh.orderservice.Client.CartClient;
    import com.mukesh.orderservice.Client.PaymentClient;
    import com.mukesh.orderservice.Client.ProductClient;
    import com.mukesh.orderservice.DTO.*;
    import com.mukesh.orderservice.Entity.Order;
    import com.mukesh.orderservice.Entity.OrderItem;
    import com.mukesh.orderservice.Entity.paymentStatus;
    import com.mukesh.orderservice.Repository.OrderItemRepository;
    import com.mukesh.orderservice.Repository.OrderRepository;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;

    import java.util.ArrayList;
    import java.util.List;

    @Service
    public class OrderService {
        public final CartClient cartClient;
        public final OrderRepository orderrepo;
        public final PaymentClient paymentClient;
        public final OrderItemRepository orderItemRepo;
        private final ProductClient productClient;
        public OrderService(CartClient cartClient, OrderRepository orderrepo, PaymentClient paymentClient, OrderItemRepository orderItemRepo, ProductClient productClient) {
            this.cartClient = cartClient;
            this.orderrepo = orderrepo;
            this.paymentClient = paymentClient;
            this.orderItemRepo = orderItemRepo;
            this.productClient = productClient;
        }
        @Transactional
        public Response placeorder(String email){
            CartResponse response= cartClient.getcartByEmail(email);
            Order order=new Order();
            List<CartItemResponse> items=response.getCartItems();
            if (items == null || items.isEmpty() || response.getTotal() == null
                    || !Double.isFinite(response.getTotal()) || response.getTotal() <= 0) {
                throw new IllegalArgumentException("Cannot place an order for an empty or invalid cart");
            }
            List<OrderItem> orderItems=new ArrayList<>();
            for(CartItemResponse item:items){
                OrderItem orderItem=new OrderItem();
                orderItem.setOrder(order);
                orderItem.setQuantity(item.getQuantity());
                orderItem.setProductId(item.getProductId());
                orderItem.setSubtotal(item.getPrice());
                orderItems.add(orderItem);
            }
            order.setItems(orderItems);
            order.setPaymentStatus(paymentStatus.PENDING);
            order.setOrdertotal(response.getTotal());
            order.setEmail(email);
            orderrepo.save(order);
            PaymentRequest paymentrequest=new PaymentRequest();
            paymentrequest.setCurrency("INR");
            paymentrequest.setAmount(order.getOrdertotal());
            paymentrequest.setOrderId(order.getOrderId());
            paymentrequest.setEmail(email);
            Response respose=new Response();
            PaymentOrderResponse paymentOrderResponse=paymentClient.CreatePayment(paymentrequest);
            respose.setPaymentorderresponse(paymentOrderResponse);
            respose.setOrderResponse(MapOrderToOrderResponse(order));
            return respose;
        }
        public List<OrderResponse>  getorders(String email) {
            List<Order> orders=orderrepo.findOrdersByEmail(email);
            List<OrderResponse> orderResponse=new ArrayList<>();
            for(Order order:orders){
               OrderResponse res=MapOrderToOrderResponse(order);
               orderResponse.add(res);
            }
            return orderResponse;
        }
        public OrderResponse getorderbyId(String orderId){
            Order order=orderrepo.findOrderByOrderId(orderId);
            if (order == null) throw new IllegalArgumentException("Order not found");
            return MapOrderToOrderResponse(order);
        }

        public OrderResponse MapOrderToOrderResponse(Order order){
            OrderResponse response=new OrderResponse();
            response.setOrderId(order.getOrderId());
            response.setPaymentStatus(order.getPaymentStatus().toString());
            List<OrderItem> items=order.getItems();
            List<OrderItemResponse> orderitemresponse=new ArrayList<>();
            for(OrderItem orderItem:items){
                OrderItemResponse res=new OrderItemResponse();
                res.setProductId(orderItem.getProductId());
                res.setQuantity(orderItem.getQuantity());
                res.setProductPrice(orderItem.getSubtotal());
                orderitemresponse.add(res);
            }
            response.setItems(orderitemresponse);
            response.setEmail(order.getEmail());
            response.setAmount(order.getOrdertotal());
            return response;
        }
        public String updateInventory(String orderId){
            Order order=orderrepo.findOrderByOrderId(orderId);
            List<OrderItem> items=order.getItems();
            for(OrderItem orderItem:items){
                InventoryRequest inventoryrequest=new InventoryRequest();
                inventoryrequest.setOrderquantity(orderItem.getQuantity());
                inventoryrequest.setProductId(orderItem.getProductId());
                productClient.UpdateInventory(inventoryrequest);
            }
            return "Inventory updated";
        }

        public String UpdatePayment(String orderId){
            Order order=orderrepo.findOrderByOrderId(orderId);
            if (order == null) throw new IllegalArgumentException("Order not found");
            order.setPaymentStatus(paymentStatus.PAID);
            orderrepo.save(order);
            return "payment updated in order Entity";
        }
    }
