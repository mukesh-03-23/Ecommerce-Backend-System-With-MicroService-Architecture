package com.mukesh.userservice.Repository;


import com.mukesh.userservice.Entity.Otp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

import java.util.Optional;

@Repository
public interface OtpRepo extends JpaRepository<Otp, Long> {
    void deleteByExpiryBefore(LocalDateTime time);

    Optional<Otp> getOtpByEmail(String email);

    void deleteOtpByEmail(String email);
}
