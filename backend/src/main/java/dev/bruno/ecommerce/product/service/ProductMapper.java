package dev.bruno.ecommerce.product.service;

import dev.bruno.ecommerce.product.entity.Product;
import dev.bruno.ecommerce.product.dto.ProductDto;
import org.springframework.stereotype.Service;

@Service
public class ProductMapper {

    public Product toProduct(ProductDto productDto) {
        return Product.builder()
                .id(productDto.id())
                .name(productDto.name())
                .price(productDto.price())
                .quantity(productDto.quantity())
                .imageUrl(productDto.imageUrl())
                .build();
    }


    public ProductDto toDto(Product product) {
        return new ProductDto(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getQuantity(),
                product.getImageUrl()
        );
    }
}
