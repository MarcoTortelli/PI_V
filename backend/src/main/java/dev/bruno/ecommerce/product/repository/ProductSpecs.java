package dev.bruno.ecommerce.product.repository;

import dev.bruno.ecommerce.product.dto.ProductFilter;
import dev.bruno.ecommerce.product.entity.Product;
import org.springframework.data.jpa.domain.Specification;

public class ProductSpecs {

    public static Specification<Product> withFilter(ProductFilter filter) {
        return Specification.where(nameContains(filter.name()));
    }

    private static Specification<Product> nameContains(String name) {
        return (root, query, criteriaBuilder) -> {

            if (name == null || name.isBlank()) {
                return null;
            }

            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("name")),
                    "%" + name.toLowerCase() + "%"
            );
        };
    }
}