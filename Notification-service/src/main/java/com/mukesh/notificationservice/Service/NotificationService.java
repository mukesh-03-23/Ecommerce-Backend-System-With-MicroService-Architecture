package com.mukesh.notificationservice.Service;

import com.mukesh.notificationservice.DTO.*;
import com.mukesh.notificationservice.Entity.Notification;
import com.mukesh.notificationservice.Repository.NotificationRepository;
import com.mukesh.notificationservice.client.OrderClient;
import com.mukesh.notificationservice.client.ProductClient;
import com.mukesh.notificationservice.client.UserClient;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class NotificationService {
    private final JavaMailSender mailSender;
    private final OrderClient orderClient;
    private final UserClient userClient;
    private final ProductClient  productClient;
    private final NotificationRepository repo;
    public NotificationService(JavaMailSender sender, OrderClient orderClient, UserClient userClient, ProductClient productClient, NotificationRepository repo) {
        this.mailSender = sender;
        this.orderClient = orderClient;
        this.userClient = userClient;
        this.productClient = productClient;
        this.repo = repo;
    }

    public String OrderConfirm(MessageDetails messagedetails){
        System.out.println("========== NOTIFICATION START ==========");

        System.out.println("Email: " + messagedetails.getEmail());
        System.out.println("Order ID: " + messagedetails.getOrderId());

        Notification notification = new Notification();
        SimpleMailMessage message=new SimpleMailMessage();
        String email=messagedetails.getEmail();
        String orderId=messagedetails.getOrderId();
        System.out.println("Calling Order Service...");
        OrderResponse orderResponse=orderClient.getorderbyId(orderId);
        System.out.println("Order Service response received");
        UserResponse res=userClient.GetuserByEmail(email);
        message.setTo(res.getEmail());
        message.setSubject("Order placed succesfully..");
        StringBuilder messageText=new StringBuilder();
        messageText.append("Dear "+res.getName());
        messageText.append("\n");
        messageText.append("You have successfully placed your order with orderId "+orderId);
        messageText.append("\n");
        messageText.append("payment successfull with razorpay order total is "+orderResponse.getAmount().toString());
        messageText.append("\n");
        messageText.append("The products are");
        messageText.append("\n");
        List<String> products=new ArrayList<>();
        List<OrderItemResponse> items=orderResponse.getItems();
        for(OrderItemResponse item:items){
            ProductResponse x;
            x=productClient.getproductById(item.getProductId());
            String name=x.productname;
            products.add(name);
        }
        for(String x:products){
            System.out.println(x);
            messageText.append(x);
            messageText.append("\n");
        }
        notification.setEmail(res.getEmail());
        notification.setName(res.getName());
        notification.setOrderTotal(orderResponse.getAmount().toString());
        notification.setEmail(email);
        repo.save(notification);
        messageText.append("Thank you visit again Mukesh store");
        message.setText(messageText.toString());
        mailSender.send(message);
        return "order placed succesfully";
    }
    public String getOtp(ForgetPasswordRequest request){
        String otp=GenerateOtp();
        String  email=request.getEmail();
        SimpleMailMessage message=new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Password Reset request");
        StringBuilder messageText=new StringBuilder();
        messageText.append("Dear "+request.getUsername());
        messageText.append("\n");
        messageText.append("Your otp for the password reset:");
        messageText.append("\n");
        messageText.append(otp);
        messageText.append("\n");
        messageText.append("This OTP expires in 3 minutes");
        message.setText(messageText.toString());
        mailSender.send(message);
        return otp;
    }
    private static String GenerateOtp(){
         final SecureRandom random=new SecureRandom();
         int otp=100000+random.nextInt(900000);
         return String.valueOf(otp);
    }
}
