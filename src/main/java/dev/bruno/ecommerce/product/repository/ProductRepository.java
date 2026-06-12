package dev.bruno.ecommerce.product.repository;

import dev.bruno.ecommerce.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long>,
        JpaSpecificationExecutor<Product> {
    boolean existsByName(String name);
    Optional<Product> findByName(String name);
}
