package com.mukesh.userservice.Service;

import com.mukesh.userservice.Entity.user;
import com.mukesh.userservice.Exceptions.UserNotFound;
import com.mukesh.userservice.Repository.UserRepo;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepo userRepo;

    public CustomUserDetailsService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        user user=userRepo.findByUseremail(email).orElseThrow(()->new UserNotFound());
        return user;
    }
}
