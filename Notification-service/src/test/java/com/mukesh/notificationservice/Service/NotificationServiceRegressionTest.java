package com.mukesh.notificationservice.Service;

import com.mukesh.notificationservice.DTO.ForgetPasswordRequest;
import com.mukesh.notificationservice.Repository.NotificationRepository;
import com.mukesh.notificationservice.client.OrderClient;
import com.mukesh.notificationservice.client.ProductClient;
import com.mukesh.notificationservice.client.UserClient;
import org.junit.jupiter.api.Test;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotificationServiceRegressionTest {
    @Test void otpHasSixDigitsAndIsSentByEmail() {
        JavaMailSender mailer = mock(JavaMailSender.class);
        NotificationService service = new NotificationService(mailer, mock(OrderClient.class),
                mock(UserClient.class), mock(ProductClient.class), mock(NotificationRepository.class));
        ForgetPasswordRequest request = new ForgetPasswordRequest();
        request.setEmail("buyer@example.com"); request.setUsername("Buyer");
        String otp = service.getOtp(request);
        assertTrue(otp.matches("[0-9]{6}"));
        var message = org.mockito.ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailer).send(message.capture());
        assertEquals("buyer@example.com", message.getValue().getTo()[0]);
        assertTrue(message.getValue().getText().contains(otp));
    }
}
