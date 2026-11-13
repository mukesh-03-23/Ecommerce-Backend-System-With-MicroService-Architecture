package com.mukesh.productservice.Service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.mukesh.productservice.DTO.*;
import com.mukesh.productservice.Entity.Product;
import com.mukesh.productservice.Entity.InventoryAdjustment;
import com.mukesh.productservice.Exceptions.ProductNotFound;
import com.mukesh.productservice.Repository.ProductRepository;
import com.mukesh.productservice.Repository.InventoryAdjustmentRepository;
import com.mukesh.productservice.Utility.MappingDtos;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProductService {
    private final Cloudinary cloudinary;
    private final ProductRepository productRepo;
    private final InventoryAdjustmentRepository adjustments;
    public ProductService(Cloudinary cloudinary, ProductRepository productRepo, InventoryAdjustmentRepository adjustments) {
        this.cloudinary = cloudinary;
        this.productRepo = productRepo;
        this.adjustments = adjustments;
    }
    public Map<String,String> addImage(MultipartFile file) throws IOException {
        Map uploaderesult=cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap("folder","ecommerce_products"));
        String url= uploaderesult.get("secure_url").toString();
        String id=uploaderesult.get("public_id").toString();
        System.out.println(uploaderesult);
        Map<String,String> map=new HashMap<>();
        map.put("id",id);
        map.put("url",url);
        return map;
    }

    public String addproduct(ProductAddRequest requestDto, MultipartFile image) throws IOException {
        if (requestDto == null || requestDto.getProductPrice() == null
                || !Double.isFinite(requestDto.getProductPrice()) || requestDto.getProductPrice() <= 0
                || requestDto.getProductQuantity() == null || requestDto.getProductQuantity() < 0) {
            throw new IllegalArgumentException("Valid product price and stock are required");
        }
        Product pro=new Product();
        pro.setProductName(requestDto.getProductName());
        pro.setProductDescription(requestDto.getProductDescription());
        pro.setProductCategory(requestDto.getProductCategory());
        pro.setProductPrice(requestDto.getProductPrice());
        pro.setProductQuantity(requestDto.getProductQuantity());
        if(image!=null && !image.isEmpty()){
            Map<String,String> imagedata=addImage(image);
            String url=imagedata.get("url");
            String id=imagedata.get("id");
            pro.setImageurl(url);
            productRepo.save(pro);
        }else{
            throw new IOException("image not found");
        }
        return "product added successfully";
    }

    public List<ProductResponseDto> getproducts() {
        List<Product> products=productRepo.findAll();
        List<ProductResponseDto> productResponseDtos=new ArrayList<>();
        for(Product product:products){
            productResponseDtos.add(MappingDtos.MapProductToProductResponse(product));
        }
        return productResponseDtos;
    }

    public ProductResponseDto getproductById(Long product_id){
        Product pro=new Product();
        pro=productRepo.findById(product_id).orElseThrow(()->new RuntimeException());
        return MappingDtos.MapProductToProductResponse(pro);
    }

    public ProductResposne findproductById(Long product_id){
        Product pro=productRepo.findById(product_id).orElseThrow(()->new ProductNotFound());
        ProductResposne res=new ProductResposne();
        res.setProductname(pro.getProductName());
        return res;
    }


    @Transactional
    public String updateInventory(InventoryRequest request){
       if (request == null || request.getProductId() == null || request.getOrderquantity() == null
               || request.getOrderquantity() <= 0 || request.getOrderId() == null
               || request.getOrderId().isBlank()) {
           throw new IllegalArgumentException("Order ID, product ID and positive quantity are required");
       }
       Product pro = productRepo.lockProduct(request.getProductId()).orElseThrow(ProductNotFound::new);
       String adjustmentId = request.getOrderId() + ":" + request.getProductId();
       if (adjustments.existsById(adjustmentId)) return "Inventory already updated for this order";
       int orderedquantity = request.getOrderquantity();
       if (pro.getProductQuantity() < orderedquantity) {
           throw new IllegalArgumentException("Insufficient stock");
       }
       pro.setProductQuantity(pro.getProductQuantity() - orderedquantity);
       productRepo.save(pro);
       adjustments.save(new InventoryAdjustment(adjustmentId));
       return "product updated successfully";
    }
    public String deleteproduct(Long product_id){
        Product pro=productRepo.findById(product_id).orElseThrow(()->new ProductNotFound());
        productRepo.delete(pro);
        return "Deleted successfully";
    }
}
