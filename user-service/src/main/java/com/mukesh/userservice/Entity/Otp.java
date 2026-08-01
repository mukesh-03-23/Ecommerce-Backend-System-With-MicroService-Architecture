package com.mukesh.userservice.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Data
public class Otp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @PrePersist
    public void setExpiry(){
        expiry= LocalDateTime.now().plusMinutes(3);
    }
    private String otp;
    @Column(unique = true)
    private String email;
    private LocalDateTime expiry;
}
