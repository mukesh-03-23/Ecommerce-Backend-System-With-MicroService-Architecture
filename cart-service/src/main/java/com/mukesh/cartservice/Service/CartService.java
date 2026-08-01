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
    public String addtocart(String email,AddCartRequest request)  {
         Cart cart=cartRepository.findByEmail(email).orElse(null);
         if(cart==null){
             cart=new Cart();
             cart.setEmail(email);
         }
        ProductResponseDto res =
                productClient.getproductById(request.getProductId());
         if(res.getProductQuantity()<=0){
            throw new OutOfStock();
         }
         if(res.getProductQuantity()<request.getQuantity()){
             throw new ReduceQuantity();
         }
        CartItem item = new CartItem();
        item.setQuantity(request.getQuantity());
        item.setPrice(res.getProductPrice()* request.getQuantity());
        item.setProductId(res.getProductId());

        cart.additem(item);

        double total = cart.getCarttotal()
                + res.getProductPrice() * request.getQuantity();

        cart.setCarttotal(total);

        cartRepository.save(cart);

        return "added to cart successfully " + total +
                " proceed to pay and place order";
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
            throw new CartNotFoundException();
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
