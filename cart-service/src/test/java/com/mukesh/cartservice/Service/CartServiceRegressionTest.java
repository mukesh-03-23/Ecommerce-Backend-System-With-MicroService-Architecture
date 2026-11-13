package com.mukesh.cartservice.Service;

import com.mukesh.cartservice.Client.ProductClient;
import com.mukesh.cartservice.DTO.AddCartRequest;
import com.mukesh.cartservice.DTO.ProductResponseDto;
import com.mukesh.cartservice.Entity.Cart;
import com.mukesh.cartservice.Repository.CartItemRepo;
import com.mukesh.cartservice.Repository.CartRepository;
import com.mukesh.cartservice.Exceptions.ReduceQuantity;
import org.junit.jupiter.api.Test;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CartServiceRegressionTest {
    private final CartRepository carts = mock(CartRepository.class);
    private final ProductClient products = mock(ProductClient.class);
    private final CartService service = new CartService(carts, products, mock(CartItemRepo.class));

    private AddCartRequest item(int quantity) {
        AddCartRequest req = new AddCartRequest();
        req.setProductId(7L); req.setQuantity(quantity);
        return req;
    }

    @Test void repeatedAddCombinesQuantityAndCorrectTotal() {
        Cart cart = new Cart(); cart.setEmail("buyer@example.com");
        ProductResponseDto product = new ProductResponseDto();
        product.setProductId(7L); product.setProductQuantity(5); product.setProductPrice(120.0);
        when(carts.findByEmail("buyer@example.com")).thenReturn(Optional.of(cart));
        when(products.getproductById(7L)).thenReturn(product);
        service.addtocart("buyer@example.com", item(1));
        service.addtocart("buyer@example.com", item(2));
        assertEquals(1, cart.getItems().size());
        assertEquals(3, cart.getItems().get(0).getQuantity());
        assertEquals(360.0, cart.getCarttotal());
        assertThrows(ReduceQuantity.class, () -> service.addtocart("buyer@example.com", item(3)));
        assertEquals(360.0, cart.getCarttotal());
    }

    @Test void clearingMissingCartCanBeRetriedSafely() {
        when(carts.findByEmail("buyer@example.com")).thenReturn(Optional.empty());
        assertEquals("Cart already empty", service.clearcart("buyer@example.com"));
        verify(carts, never()).deleteCartByEmail(anyString());
    }
}
