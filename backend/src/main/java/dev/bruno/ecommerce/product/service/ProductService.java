package dev.bruno.ecommerce.product.service;

import dev.bruno.ecommerce.product.dto.ProductFilter;
import dev.bruno.ecommerce.product.entity.Product;
import dev.bruno.ecommerce.product.dto.ProductDto;
import dev.bruno.ecommerce.exception.DuplicateProductException;
import dev.bruno.ecommerce.exception.EntityNotFoundException;
import dev.bruno.ecommerce.product.repository.ProductRepository;
import dev.bruno.ecommerce.product.repository.ProductSpecs;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductDto createProduct(ProductDto productDto) {
        if (productRepository.existsByName(productDto.name())) {
            throw new DuplicateProductException(productDto.name());
        }
        Product savedProduct = productRepository.save(productMapper.toProduct(productDto));

        return productMapper.toDto(savedProduct);
    }

    public ProductDto findProductById(Long id) {
        return productMapper.toDto(productRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Entity with ID %d not found.", id))
        ));
    }

    public Page<ProductDto> search(ProductFilter filter, Pageable pageable) {

        Specification<Product> specification =
                Specification.where(ProductSpecs.withFilter(filter));

        Page<Product> products = productRepository.findAll(specification, pageable);

        return products.map(productMapper::toDto);
    }

    @Transactional
    public ProductDto updateProductById(Long id, ProductDto productDto) {
        Product product = productRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Entity with ID %d not found.", id))
        );

        product.setName(productDto.name());
        product.setPrice(productDto.price());
        product.setQuantity(productDto.quantity());

        return productMapper.toDto(product);
    }

    public void deleteProductById(Long id) {
        Product product = productRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Entity with ID %d not found.", id))
        );

        productRepository.delete(product);
    }
}
