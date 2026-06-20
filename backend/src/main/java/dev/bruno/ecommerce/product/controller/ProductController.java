package dev.bruno.ecommerce.product.controller;

import dev.bruno.ecommerce.product.dto.ProductDto;
import dev.bruno.ecommerce.product.dto.ProductFilter;
import dev.bruno.ecommerce.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/product")
@Tag(name = "Product", description = "Endpoints for managing products in the marketplace")
public class ProductController {
    private final ProductService service;

    public ProductController(ProductService productService) {
        this.service = productService;
    }

    @Operation(
            summary = "Create a product",
            description = "Creates a new product and stores it in the database."
    )
    @PostMapping("/create-product")
    public ResponseEntity<ProductDto> createProduct(@RequestBody @Valid ProductDto createProductDto) {
        ProductDto productDto = service.createProduct(createProductDto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/{id}")
                .buildAndExpand(productDto.id())
                .toUri();

        return ResponseEntity.created(location).body(productDto);
    }

    @Operation(
            summary = "Find product by ID",
            description = "Retrieves a product using its unique identifier."
    )
    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> findProductById(@PathVariable Long id) {
        ProductDto productDto = service.findProductById(id);

        return ResponseEntity.ok().body(productDto);
    }

    @Operation(
            summary = "Search products",
            description = "Searches products using optional filters such as product name with pagination support."
    )
    @GetMapping
    public ResponseEntity<Page<ProductDto>> searchProducts(
            ProductFilter filter,
            Pageable pageable
    ) {

        Page<ProductDto> products = service.search(filter, pageable);

        return ResponseEntity.ok(products);
    }

    @Operation(
            summary = "Update product",
            description = "Updates an existing product using its unique identifier."
    )
    @PutMapping("/update-product/{id}")
    public ResponseEntity<ProductDto> updateProductById(@PathVariable Long id, @RequestBody @Valid ProductDto productDto) {
        ProductDto updatedProductDto = service.updateProductById(id, productDto);

        return ResponseEntity.ok().body(updatedProductDto);
    }

    @Operation(
            summary = "Delete product",
            description = "Deletes a product from the database using its unique identifier."
    )
    @DeleteMapping("/delete-product/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        service.deleteProductById(id);

        return ResponseEntity.noContent().build();
    }
}
