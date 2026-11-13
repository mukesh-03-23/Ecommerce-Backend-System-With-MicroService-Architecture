package com.mukesh.productservice.Service;

import com.cloudinary.Cloudinary;
import com.mukesh.productservice.DTO.InventoryRequest;
import com.mukesh.productservice.Entity.Product;
import com.mukesh.productservice.Repository.InventoryAdjustmentRepository;
import com.mukesh.productservice.Repository.ProductRepository;
import org.junit.jupiter.api.Test;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceRegressionTest {
    private final ProductRepository products = mock(ProductRepository.class);
    private final InventoryAdjustmentRepository adjustments = mock(InventoryAdjustmentRepository.class);
    private final ProductService service = new ProductService(mock(Cloudinary.class), products, adjustments);

    private InventoryRequest request(int quantity) {
        InventoryRequest r = new InventoryRequest();
        r.setProductId(1L); r.setOrderId("ORD-ONE"); r.setOrderquantity(quantity);
        return r;
    }

    @Test void rejectsInvalidAndOverdraftStock() {
        Product product = new Product();
        product.setProductQuantity(3);
        when(products.lockProduct(1L)).thenReturn(Optional.of(product));
        assertThrows(IllegalArgumentException.class, () -> service.updateInventory(request(4)));
        assertThrows(IllegalArgumentException.class, () -> service.updateInventory(request(-1)));
        assertEquals(3, product.getProductQuantity());
        verify(products, never()).save(any());
    }

    @Test void identicalOrderCannotDeductTwice() {
        Product product = new Product();
        product.setProductQuantity(5);
        when(products.lockProduct(1L)).thenReturn(Optional.of(product));
        when(adjustments.existsById("ORD-ONE:1")).thenReturn(false, true);
        assertEquals("product updated successfully", service.updateInventory(request(2)));
        assertEquals("Inventory already updated for this order", service.updateInventory(request(2)));
        assertEquals(3, product.getProductQuantity());
        verify(products, times(1)).save(any());
        verify(adjustments, times(1)).save(any());
    }
}
