package com.mukesh.paymentservice.Repository;

import com.mukesh.paymentservice.Entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, String> {
   Optional<Payment> findByOrderId(String orderId);

    Payment getPaymentByRazorpayPaymentId(String razorpayPaymentId);

    Payment getPaymentByRazorpayOrderId(String razorpayOrderId);
}
