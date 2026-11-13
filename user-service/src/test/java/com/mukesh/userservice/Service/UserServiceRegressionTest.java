package com.mukesh.userservice.Service;

import com.mukesh.userservice.Client.NotificationClient;
import com.mukesh.userservice.DTO.RegisterRequest;
import com.mukesh.userservice.DTO.ResetPasswordRequest;
import com.mukesh.userservice.Entity.Otp;
import com.mukesh.userservice.Entity.user;
import com.mukesh.userservice.Exceptions.WrongCredentials;
import com.mukesh.userservice.Jwtutility.JwtServices;
import com.mukesh.userservice.Repository.OtpRepo;
import com.mukesh.userservice.Repository.UserRepo;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.LocalDateTime;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceRegressionTest {
    private final UserRepo users = mock(UserRepo.class);
    private final OtpRepo otps = mock(OtpRepo.class);
    private final PasswordEncoder encoder = mock(PasswordEncoder.class);
    private final UserService service = new UserService(users, encoder, mock(JwtServices.class),
            mock(NotificationClient.class), otps);

    @Test void registrationRejectsDifferentPasswords() {
        RegisterRequest request = new RegisterRequest();
        request.setPassword("first"); request.setConfirmPassword("second");
        assertThrows(IllegalArgumentException.class, () -> service.register(request));
        verify(users, never()).save(any());
    }
    @Test void expiredOtpCannotResetPassword() {
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setEmail("buyer@example.com"); request.setOtp("123456");
        request.setPassword("newpassword"); request.setConfirmPassword("newpassword");
        Otp otp = new Otp();
        otp.setOtp("123456"); otp.setExpiry(LocalDateTime.now().minusMinutes(1));
        when(users.findByUseremail("buyer@example.com")).thenReturn(Optional.of(new user()));
        when(otps.getOtpByEmail("buyer@example.com")).thenReturn(Optional.of(otp));
        assertThrows(WrongCredentials.class, () -> service.resetpassword(request));
        verify(users, never()).save(any());
    }
}
