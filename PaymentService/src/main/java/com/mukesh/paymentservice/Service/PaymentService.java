package com.mukesh.paymentservice.Service;


import com.mukesh.paymentservice.Client.CartClient;
import com.mukesh.paymentservice.Client.NotificationClient;
import com.mukesh.paymentservice.Client.OrderClient;
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



@Service
public class PaymentService {
    private final RazorpayClient razorpayClient;
    private final PaymentRepository paymentRepository;
    private final CartClient cartClient;
    private final NotificationClient notificationClient;
    private final OrderClient  orderClient;
    public PaymentService(RazorpayClient razorpayClient, PaymentRepository paymentRepository, CartClient cartClient, NotificationClient notificationClient, OrderClient orderClient) {
        this.razorpayClient = razorpayClient;
        this.paymentRepository = paymentRepository;
        this.cartClient = cartClient;
        this.notificationClient = notificationClient;
        this.orderClient = orderClient;
    }

    public PaymentOrderResponse CreateOrder(PaymentRequest request) throws RazorpayException {
        JSONObject object = new JSONObject();
        int amountInpaise = (int) (request.getAmount() * 100);
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
        System.out.println("calling verify payment...");
        String secretkey="phcm9tlAyBCkgUNqZpNC91IJ";
        JSONObject payload=new JSONObject();
        payload.put("razorpay_order_id",request.getRazorpayOrderId());
        payload.put("razorpay_payment_id",request.getRazorpayPaymentId());
        payload.put("razorpay_signature",request.getRazorpaySignature());

        if(Utils.verifyPaymentSignature(payload,secretkey)){
            Payment payment =paymentRepository.getPaymentByRazorpayOrderId((request.getRazorpayOrderId()));
            payment.setRazorpayPaymentId(request.getRazorpayPaymentId());
            payment.setStatus(PaymentStatus.SUCCESS);
            paymentRepository.save(payment);
            MessageDetails details =new MessageDetails();
            details.setOrderId(payment.getOrderId());
            details.setEmail(payment.getEmail());
            notificationClient.Orderconfirm(details);
            cartClient.clearcart(payment.getEmail());
            orderClient.updateInventory(details.getOrderId());
            orderClient.UpdatePayment(details.getOrderId());
            return "payment Done";
        }
        throw new RazorpayException("Payment Failed");
    }
}