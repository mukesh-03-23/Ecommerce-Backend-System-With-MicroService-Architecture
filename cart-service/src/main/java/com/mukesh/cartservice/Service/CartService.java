package com.mukesh.cartservice.Service;

import com.mukesh.cartservice.Client.ProductClient;
import com.mukesh.cartservice.DTO.AddCartRequest;
import com.mukesh.cartservice.DTO.CartItemResponse;
import com.mukesh.cartservice.DTO.CartResponse;
import com.mukesh.cartservice.DTO.ProductResponseDto;
import com.mukesh.cartservice.Entity.Cart;
import com.mukesh.cartservice.Entity.CartItem;
import com.mukesh.cartservice.Exceptions.CartNotFoundException;
import com.mukesh.cartservice.Exceptions.OutOfStock;
import com.mukesh.cartservice.Exceptions.ReduceQuantity;
import com.mukesh.cartservice.Repository.CartItemRepo;
import com.mukesh.cartservice.Repository.CartRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class CartService {
    private final CartRepository cartRepository;
    private final ProductClient productClient;
    private final CartItemRepo cartItemRepo;

    public CartService(CartRepository cartRepository,ProductClient client, CartItemRepo cartItemRepo) {
        this.cartRepository = cartRepository;
        this.productClient = client;
        this.cartItemRepo = cartItemRepo;
    }
    @Transactional
    public String addtocart(String email, AddCartRequest request) {
        if (request == null || request.getProductId() == null || request.getQuantity() == null
                || request.getQuantity() <= 0) {
            throw new IllegalArgumentException("A product and a positive quantity are required");
        }
        ProductResponseDto product = productClient.getproductById(request.getProductId());
        if (product == null || product.getProductQuantity() == null
                || product.getProductPrice() == null || !Double.isFinite(product.getProductPrice())
                || product.getProductPrice() < 0) {
            throw new IllegalArgumentException("Invalid product details");
        }
        Cart cart = cartRepository.findByEmail(email).orElse(null);
        if (cart == null) {
            cart = new Cart();
            cart.setEmail(email);
        }
        CartItem existing = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(request.getProductId()))
                .findFirst().orElse(null);
        long desired = (long) request.getQuantity() + (existing == null ? 0 : existing.getQuantity());
        if (product.getProductQuantity() <= 0) throw new OutOfStock();
        if (desired > product.getProductQuantity()) throw new ReduceQuantity();

        if (existing == null) {
            existing = new CartItem();
            existing.setProductId(request.getProductId());
            cart.additem(existing);
        }
        existing.setQuantity((int) desired);
        existing.setPrice(product.getProductPrice() * desired);
        double total = cart.getItems().stream().mapToDouble(CartItem::getPrice).sum();
        cart.setCarttotal(total);
        cartRepository.save(cart);
        return "added to cart successfully " + total + " proceed to pay and place order";
    }

    public CartResponse getCartByUserId(String email) {
        Cart cart=cartRepository.findByEmail(email).orElse(null);
        if(cart==null){
            throw new CartNotFoundException();
        }
        List<CartItemResponse> temps=new ArrayList<>();
        List<CartItem> cartItems=cart.getItems();
        for (CartItem item : cartItems) {
            CartItemResponse itemResponse=new CartItemResponse();
            itemResponse.setProductId(item.getProductId());
            itemResponse.setQuantity(item.getQuantity());
            itemResponse.setPrice(item.getPrice());
            temps.add(itemResponse);
        }
        CartResponse cartResponse = new CartResponse();
        cartResponse.setCartItems(temps);
        cartResponse.setEmail(email);
        cartResponse.setTotal(cart.getCarttotal());
        return cartResponse;
    }

    @Transactional
    public String clearcart(String email) {
        Cart cart=cartRepository.findByEmail(email).orElse(null);
        if(cart==null){
            return "Cart already empty";
        }
        cartRepository.deleteCartByEmail(email);
        return "removed successfully";
    }


    public List<Long> getproductIds(String email) {
        List<Long> ans=new ArrayList<>();
        Cart cart=cartRepository.findByEmail(email).orElse(null);
        if(cart==null){
            throw new CartNotFoundException();
        }
        List<CartItem> cartItems=cart.getItems();
        for (CartItem item : cartItems) {
            ans.add(item.getProductId());
        }
        return ans;
    }
}
