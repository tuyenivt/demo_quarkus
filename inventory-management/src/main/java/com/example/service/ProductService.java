package com.example.service;

import com.example.entity.Product;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class ProductService {
    public Uni<List<Product>> listAll() {
        return Product.listAll();
    }

    public Uni<Product> findById(Long id) {
        return Product.findById(id);
    }

    public Uni<Product> create(Product product) {
        return product.persist().replaceWith(product);
    }

    public Uni<Product> update(Long id, Product updatedProduct) {
        return Product.<Product>findById(id)
                .onItem().ifNotNull().invoke(existing -> {
                    existing.name = updatedProduct.name;
                    existing.sku = updatedProduct.sku;
                    existing.description = updatedProduct.description;
                });
    }

    public Uni<Void> delete(Long id) {
        return Product.deleteById(id).replaceWithVoid();
    }
}
