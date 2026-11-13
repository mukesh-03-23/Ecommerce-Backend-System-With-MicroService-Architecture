package com.mukesh.productservice.Repository;

import com.mukesh.productservice.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Product p where p.product_id = :productId")
    Optional<Product> lockProduct(@Param("productId") Long productId);
}
