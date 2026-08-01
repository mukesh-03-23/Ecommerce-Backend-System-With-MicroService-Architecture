package com.mukesh.productservice.Service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.mukesh.productservice.DTO.*;
import com.mukesh.productservice.Entity.Product;
import com.mukesh.productservice.Exceptions.ProductNotFound;
import com.mukesh.productservice.Repository.ProductRepository;
import com.mukesh.productservice.Utility.MappingDtos;
import org.springframework.stereotype.Service;
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
    public ProductService(Cloudinary cloudinary, ProductRepository productRepo) {
        this.cloudinary = cloudinary;
        this.productRepo = productRepo;
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


    public String updateInventory(InventoryRequest request){
       Product pro=productRepo.findById(request.getProductId()).orElseThrow(()->new ProductNotFound());
       int orderedquantity= request.getOrderquantity();
       int productquantity=pro.getProductQuantity();
       if(productquantity==0){
           throw new ProductNotFound();
       }
       pro.setProductQuantity(productquantity-orderedquantity);
       productRepo.save(pro);
       return "product updated successfully";
    }
    public String deleteproduct(Long product_id){
        Product pro=productRepo.findById(product_id).orElseThrow(()->new ProductNotFound());
        productRepo.delete(pro);
        return "Deleted successfully";
    }
}
