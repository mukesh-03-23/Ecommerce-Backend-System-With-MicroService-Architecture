package com.mukesh.cartservice.Entity;

import com.mukesh.cartservice.Exceptions.CartEmptyException;
import com.mukesh.cartservice.Service.CartService;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(
        name="cart",
        uniqueConstraints={
                @UniqueConstraint(columnNames = "email")
        }
)
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartId;
    private String email;
    private double carttotal;
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<CartItem> items=new ArrayList<>();

    public void additem(CartItem cartItem) {
        items.add(cartItem);
        cartItem.setCart(this);
    }
    public void removeitem(CartItem cartItem) {
        if(items.contains(cartItem)) {
            items.remove(cartItem);
        }else{
            throw new RuntimeException(new CartEmptyException());
        }
        cartItem.setCart(null);
    }
}
