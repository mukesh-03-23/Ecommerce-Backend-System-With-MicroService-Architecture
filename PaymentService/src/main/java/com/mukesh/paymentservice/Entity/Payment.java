    package com.mukesh.paymentservice.Entity;

    import jakarta.persistence.*;
    import lombok.Data;

    import java.util.UUID;

    @Entity
    @Data
    @Table(name="payments")
    public class Payment {
        @Id
        private String paymantId;
        @PrePersist
        public void Payment() {
            this.paymantId= "PAY"+UUID.randomUUID().toString().substring(0,8);
        }
        private String OrderId;
        private String email;
        private Double amount;
        private String razorpayOrderId;
        private String razorpayPaymentId;
        @Enumerated(EnumType.STRING)
        private PaymentStatus status;
        private String currency;
    }
