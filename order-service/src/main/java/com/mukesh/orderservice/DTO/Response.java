package com.mukesh.orderservice.DTO;

import lombok.Data;

@Data
public class Response {
    private OrderResponse orderResponse;
    private PaymentOrderResponse paymentorderresponse;
}
