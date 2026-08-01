package com.mukesh.cartservice.Repository;

import com.mukesh.cartservice.Entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.Optional;
import java.util.UUID;

@Repository
public interface CartRepository extends JpaRepository<Cart,Long> {
    Optional<Cart> findByEmail(String email);

    void deleteCartByEmail(String email);
}
