package com.mukesh.orderservice.Controller;

import com.mukesh.orderservice.DTO.OrderResponse;
import com.mukesh.orderservice.DTO.Response;
import com.mukesh.orderservice.Service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/order")
public class OrderCotroller {
    private final OrderService orderservice;
    public OrderCotroller(OrderService orderservice) {
        this.orderservice = orderservice;
    }
    @PostMapping("/placeorder")
    public ResponseEntity<Response> placeorder(@RequestHeader("useremail") String email) {
        return new ResponseEntity<>(orderservice.placeorder(email),HttpStatus.OK);
    }
    @GetMapping("/getorders")
    public ResponseEntity<List<OrderResponse>> getorders(@RequestHeader("useremail") String email) {
        return new ResponseEntity<>(orderservice.getorders(email),HttpStatus.OK);
    }
    @GetMapping("/getorder/{orderId}")
    public ResponseEntity<OrderResponse> getorderbyId(@PathVariable String orderId) {
        return new ResponseEntity<>(orderservice.getorderbyId(orderId),HttpStatus.OK);
    }
    @PostMapping("/updateInventory/{orderId}")
    public ResponseEntity<String> updateInventory(@PathVariable String orderId){
        return new ResponseEntity<>(orderservice.updateInventory(orderId),HttpStatus.OK);
    }
    @PostMapping("/updatepayment/{orderId}")
    public ResponseEntity<String> UpdatePayment(@PathVariable String orderId){
        return new ResponseEntity<>(orderservice.UpdatePayment(orderId),HttpStatus.OK);
    }
}
