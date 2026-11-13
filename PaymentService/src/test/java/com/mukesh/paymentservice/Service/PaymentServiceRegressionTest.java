package com.mukesh.paymentservice.Service;

import com.mukesh.paymentservice.Client.CartClient;
import com.mukesh.paymentservice.Client.NotificationClient;
import com.mukesh.paymentservice.Client.OrderClient;
import com.mukesh.paymentservice.Configuration.RazorpayClientConfig;
import com.mukesh.paymentservice.DTO.VerifyPaymentRequest;
import com.mukesh.paymentservice.Entity.Payment;
import com.mukesh.paymentservice.Entity.PaymentStatus;
import com.mukesh.paymentservice.Repository.PaymentRepository;
import com.razorpay.RazorpayClient;
import org.junit.jupiter.api.Test;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentServiceRegressionTest {
    private final PaymentRepository repository = mock(PaymentRepository.class);
    private final CartClient cart = mock(CartClient.class);
    private final OrderClient orders = mock(OrderClient.class);
    private final NotificationClient notifications = mock(NotificationClient.class);
    private final RazorpayClientConfig config = new RazorpayClientConfig();
    private final PaymentService service;

    PaymentServiceRegressionTest() {
        config.secretkey = "test_secret_only";
        service = new PaymentService(mock(RazorpayClient.class), repository, cart, notifications, orders, config);
    }

    private VerifyPaymentRequest signedRequest() throws Exception {
        String orderId = "order_test_one", paymentId = "pay_test_one";
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(config.secretkey.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        String signature = HexFormat.of().formatHex(mac.doFinal((orderId + "|" + paymentId).getBytes(StandardCharsets.UTF_8)));
        VerifyPaymentRequest request = new VerifyPaymentRequest();
        request.setRazorpayOrderId(orderId); request.setRazorpayPaymentId(paymentId);
        request.setRazorpaySignature(signature);
        return request;
    }

    @Test void successfulReplayDoesNotDeductInventoryAgain() throws Exception {
        Payment payment = new Payment();
        payment.setOrderId("ORD-A"); payment.setEmail("buyer@example.com");
        payment.setStatus(PaymentStatus.CREATED);
        when(repository.getPaymentByRazorpayOrderId("order_test_one")).thenReturn(payment);
        VerifyPaymentRequest request = signedRequest();
        assertEquals("payment Done", service.verifypayment(request));
        assertEquals(PaymentStatus.SUCCESS, payment.getStatus());
        assertEquals("Payment already confirmed", service.verifypayment(request));
        verify(orders, times(1)).updateInventory("ORD-A");
        verify(cart, times(1)).clearcart("buyer@example.com");
    }
}
