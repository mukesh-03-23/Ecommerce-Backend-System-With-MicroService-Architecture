package com.mukesh.paymentservice.Service;


import com.mukesh.paymentservice.Client.CartClient;
import com.mukesh.paymentservice.Client.NotificationClient;
import com.mukesh.paymentservice.Client.OrderClient;
import com.mukesh.paymentservice.Configuration.RazorpayClientConfig;
import com.mukesh.paymentservice.DTO.MessageDetails;
import com.mukesh.paymentservice.DTO.PaymentOrderResponse;
import com.mukesh.paymentservice.DTO.PaymentRequest;
import com.mukesh.paymentservice.DTO.VerifyPaymentRequest;
import com.mukesh.paymentservice.Entity.Payment;
import com.mukesh.paymentservice.Entity.PaymentStatus;
import com.mukesh.paymentservice.Repository.PaymentRepository;
import com.razorpay.*;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;



@Service
public class PaymentService {
    private final RazorpayClient razorpayClient;
    private final PaymentRepository paymentRepository;
    private final CartClient cartClient;
    private final NotificationClient notificationClient;
    private final OrderClient  orderClient;
    private final RazorpayClientConfig config;
    public PaymentService(RazorpayClient razorpayClient, PaymentRepository paymentRepository, CartClient cartClient, NotificationClient notificationClient, OrderClient orderClient, RazorpayClientConfig config) {
        this.razorpayClient = razorpayClient;
        this.paymentRepository = paymentRepository;
        this.cartClient = cartClient;
        this.notificationClient = notificationClient;
        this.orderClient = orderClient;
        this.config = config;
    }

    public PaymentOrderResponse CreateOrder(PaymentRequest request) throws RazorpayException {
        JSONObject object = new JSONObject();
        if (request == null || request.getAmount() == null || !Double.isFinite(request.getAmount())
                || request.getAmount() <= 0 || request.getAmount() > Integer.MAX_VALUE / 100.0) {
            throw new IllegalArgumentException("Invalid payment amount");
        }
        int amountInpaise = (int) Math.round(request.getAmount() * 100);
        object.put("amount", amountInpaise);
        object.put("currency", request.getCurrency());
        String receipt = "REC" + request.getOrderId() + "RZ";
        object.put("receipt", receipt);
        Order order = razorpayClient.orders.create(object);
        Payment payment=new Payment();
        payment.setStatus(PaymentStatus.CREATED);
        payment.setOrderId(request.getOrderId());
        payment.setAmount(request.getAmount());
        payment.setCurrency(request.getCurrency());
        payment.setRazorpayOrderId(order.get("id"));
        payment.setEmail(request.getEmail());
        paymentRepository.save(payment);
        PaymentOrderResponse response = new PaymentOrderResponse();
        response.setCurrency(request.getCurrency());
        response.setOrder_Id(request.getOrderId());
        response.setRazorpayorderId(order.get("id"));
        response.setAmount(request.getAmount());
        response.setEmail(request.getEmail());
        return response;
    }

    public String verifypayment(VerifyPaymentRequest request) throws RazorpayException {
        if (request == null || request.getRazorpayOrderId() == null
                || request.getRazorpayPaymentId() == null || request.getRazorpaySignature() == null) {
            throw new IllegalArgumentException("Payment verification details are required");
        }
        JSONObject payload = new JSONObject();
        payload.put("razorpay_order_id", request.getRazorpayOrderId());
        payload.put("razorpay_payment_id", request.getRazorpayPaymentId());
        payload.put("razorpay_signature", request.getRazorpaySignature());
        if (!Utils.verifyPaymentSignature(payload, config.secretkey)) {
            throw new RazorpayException("Payment signature verification failed");
        }
        Payment payment = paymentRepository.getPaymentByRazorpayOrderId(request.getRazorpayOrderId());
        if (payment == null) throw new IllegalArgumentException("Payment order not found");
        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            if (!request.getRazorpayPaymentId().equals(payment.getRazorpayPaymentId())) {
                throw new IllegalArgumentException("Payment reference mismatch");
            }
            return "Payment already confirmed";
        }
        // Preserve existing synchronous Feign sequence. Treat email notification as
        // post-payment best-effort so SMTP outages do not invalidate verified payments.
        orderClient.updateInventory(payment.getOrderId());
        orderClient.UpdatePayment(payment.getOrderId());
        cartClient.clearcart(payment.getEmail());
        payment.setRazorpayPaymentId(request.getRazorpayPaymentId());
        payment.setStatus(PaymentStatus.SUCCESS);
        paymentRepository.save(payment);
        MessageDetails details = new MessageDetails();
        details.setOrderId(payment.getOrderId());
        details.setEmail(payment.getEmail());
        try {
            notificationClient.Orderconfirm(details);
        } catch (Exception notificationError) {
            org.slf4j.LoggerFactory.getLogger(PaymentService.class)
                    .warn("Payment confirmed, but email notification failed for order {}",
                            payment.getOrderId(), notificationError);
        }
        return "payment Done";
    }
}
