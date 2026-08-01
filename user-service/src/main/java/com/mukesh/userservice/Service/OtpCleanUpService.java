package com.mukesh.userservice.Service;

import com.mukesh.userservice.Repository.OtpRepo;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
public class OtpCleanUpService {
    private final OtpRepo otpRepo;

    public OtpCleanUpService(OtpRepo otpRepo) {
        this.otpRepo = otpRepo;
    }

    @Scheduled(fixedRate = 60000*3)
    public void cleanOtp(){
        otpRepo.deleteByExpiryBefore(LocalDateTime.now());
    }
}
