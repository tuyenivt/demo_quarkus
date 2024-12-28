package com.example.resource;

import com.example.entity.Product;
import com.example.service.ProductService;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductResource {
    private final ProductService productService;
    
    @Inject
    public ProductResource(ProductService productService) {
        this.productService = productService;
    }

    @GET
    public Uni<List<Product>> getAll() {
        return productService.listAll();
    }

    @GET
    @Path("/{id}")
    public Uni<Product> getById(@PathParam("id") Long id) {
        return productService.findById(id);
    }

    @POST
    public Uni<Product> create(Product product) {
        return productService.create(product);
    }

    @PUT
    @Path("/{id}")
    public Uni<Product> updateById(@PathParam("id") Long id, Product product) {
        return productService.update(id, product);
    }

    @DELETE
    @Path("/{id}")
    public Uni<Void> deleteById(@PathParam("id") Long id) {
        return productService.delete(id);
    }
}
