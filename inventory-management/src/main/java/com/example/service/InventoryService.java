package com.example.service;

import com.example.entity.Inventory;
import com.example.entity.Product;
import com.example.ws.InventoryWebSocket;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.util.List;

@ApplicationScoped
public class InventoryService {
    private static final Logger LOG = Logger.getLogger(InventoryService.class);

    private final ObjectMapper objectMapper;

    @Inject
    public InventoryService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public Uni<List<Inventory>> listAll() {
        return Inventory.listAll();
    }

    public Uni<Inventory> findById(Long id) {
        return Inventory.findById(id);
    }

    public Uni<Inventory> create(Inventory inventory) {
        return Product.<Product>findById(inventory.product.id)
                .onItem().ifNotNull().invoke(product -> inventory.product = product)
                .chain(() -> inventory.persist().replaceWith(inventory))
                .invoke(this::broadcastUpdate);
    }

    public Uni<Inventory> update(Long id, Inventory updatedInventory) {
        return Inventory.<Inventory>findById(id)
                .onItem().ifNotNull().invoke(existing -> {
                    existing.quantity = updatedInventory.quantity;
                    existing.product = updatedInventory.product;
                })
                .invoke(this::broadcastUpdate);
    }

    public Uni<Void> delete(Long id) {
        return Inventory.deleteById(id)
                .invoke(() -> broadcastDeletion(id))
                .replaceWithVoid();
    }

    public Uni<Inventory> adjustQuantity(Long id, int adjustment) {
        return Inventory.<Inventory>findById(id)
                .onItem().ifNotNull().invoke(existing -> existing.quantity += adjustment)
                .invoke(this::broadcastUpdate);
    }

    private void broadcastUpdate(Inventory inventory) {
        try {
            var message = objectMapper.writeValueAsString(inventory);
            InventoryWebSocket.broadcast(message);
        } catch (JsonProcessingException e) {
            LOG.error("Failed to broadcast inventory update", e);
        }
    }

    private void broadcastDeletion(Long id) {
        try {
            var message = "{\"deletedId\":" + id + "}";
            InventoryWebSocket.broadcast(message);
        } catch (Exception e) {
            LOG.error("Failed to broadcast inventory deletion", e);
        }
    }
}
