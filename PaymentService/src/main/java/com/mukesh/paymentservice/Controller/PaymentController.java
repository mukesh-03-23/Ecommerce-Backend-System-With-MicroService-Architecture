package com.mukesh.paymentservice.Controller;

import com.mukesh.paymentservice.DTO.PaymentOrderResponse;
import com.mukesh.paymentservice.DTO.PaymentRequest;
import com.mukesh.paymentservice.DTO.VerifyPaymentRequest;
import com.mukesh.paymentservice.Service.PaymentService;
import com.razorpay.RazorpayException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
    @PostMapping("/createorder")
    public ResponseEntity<PaymentOrderResponse> CreatePayment(@RequestBody PaymentRequest request) throws RazorpayException {
        return new ResponseEntity<>(paymentService.CreateOrder(request), HttpStatus.OK);
    }
    @PostMapping("/verify")
    public ResponseEntity<String> verifypayment(@RequestBody VerifyPaymentRequest verifypaymentRequest) throws RazorpayException {
        return new ResponseEntity<>(paymentService.verifypayment(verifypaymentRequest),HttpStatus.OK);
    }
}
