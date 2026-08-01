package com.mukesh.userservice.Repository;

import com.mukesh.userservice.Entity.user;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<user,String> {
    Optional<user> findByUseremail(String useremail);
}

