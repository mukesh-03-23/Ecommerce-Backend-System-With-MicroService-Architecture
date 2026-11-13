package com.mukesh.productservice.Controller;


import com.mukesh.productservice.DTO.*;
import com.mukesh.productservice.Service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {
    private final ProductService prodservice;
    public ProductController(ProductService prodservice) {
        this.prodservice = prodservice;
    }

    @PostMapping("/addproduct")
    public ResponseEntity<String> addProduct(@RequestPart("requestDto") ProductAddRequest requestDto,@RequestPart("image") MultipartFile image) throws IOException {
        return new ResponseEntity<>(prodservice.addproduct(requestDto,image), HttpStatus.OK);
    }
    @GetMapping("/getproducts")
    public ResponseEntity<List<ProductResponseDto>> getproducts(){
        return new ResponseEntity<>(prodservice.getproducts(),HttpStatus.OK);
    }
    @GetMapping("/getproduct/{product_id}")
    public ResponseEntity<ProductResponseDto> getproductById(@PathVariable Long product_id){
        return new ResponseEntity<>(prodservice.getproductById(product_id),HttpStatus.OK);
    }
    @GetMapping("/findproduct/{prodId}")
    public ResponseEntity<ProductResposne> findproductById(@PathVariable Long prodId){
        return new ResponseEntity<>(prodservice.findproductById(prodId),HttpStatus.OK);
    }
    @PostMapping("/Inventoryupdate")
    public ResponseEntity<String> UpdateInventory(@RequestBody InventoryRequest request){
        return new ResponseEntity<>(prodservice.updateInventory(request),HttpStatus.OK);
    }
    public ResponseEntity<String> deleteproduct(@RequestHeader Long product_id){
        return new ResponseEntity<>(prodservice.deleteproduct(product_id),HttpStatus.OK);
    }
}