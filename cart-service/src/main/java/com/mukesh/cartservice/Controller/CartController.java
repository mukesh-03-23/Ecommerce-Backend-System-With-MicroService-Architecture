package com.mukesh.cartservice.Controller;
import com.mukesh.cartservice.DTO.AddCartRequest;
import com.mukesh.cartservice.DTO.CartResponse;
import com.mukesh.cartservice.Service.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/cart")
public class CartController {
    private final CartService cartservice;

    public CartController(CartService cartservice) {
        this.cartservice = cartservice;
    }
    @PostMapping("/addcart")
    public ResponseEntity<String> addtocart(@RequestHeader("useremail") String email, @RequestBody AddCartRequest addCartRequest) {
        return new ResponseEntity<>(cartservice.addtocart(email,addCartRequest), HttpStatus.OK);
    }
    @GetMapping("/getcart/{email}")
    public ResponseEntity<CartResponse> getcartByEmail(@PathVariable String email) {
        return new ResponseEntity<>(cartservice.getCartByUserId(email),HttpStatus.OK);
    }
    @DeleteMapping("/clear-cart/{email}")
    public ResponseEntity<String> clearcart(@PathVariable String email) {
        return new ResponseEntity<>(cartservice.clearcart(email),HttpStatus.OK);
    }
    @GetMapping("/productIds")
    public ResponseEntity<List<Long>> getproductIds(String email) {
        return new ResponseEntity<>(cartservice.getproductIds(email),HttpStatus.OK);
    }
}
