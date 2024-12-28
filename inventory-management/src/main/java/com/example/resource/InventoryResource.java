package com.example.resource;

import com.example.entity.Inventory;
import com.example.service.InventoryService;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/inventories")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class InventoryResource {
    private final InventoryService inventoryService;

    @Inject
    public InventoryResource(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GET
    public Uni<List<Inventory>> getAll() {
        return inventoryService.listAll();
    }

    @GET
    @Path("/{id}")
    public Uni<Inventory> getById(@PathParam("id") Long id) {
        return inventoryService.findById(id);
    }

    @POST
    public Uni<Inventory> create(Inventory inventory) {
        return inventoryService.create(inventory);
    }

    @PUT
    @Path("/{id}")
    public Uni<Inventory> updateById(@PathParam("id") Long id, Inventory inventory) {
        return inventoryService.update(id, inventory);
    }

    @DELETE
    @Path("/{id}")
    public Uni<Void> deleteById(@PathParam("id") Long id) {
        return inventoryService.delete(id);
    }

    @POST
    @Path("/{id}/adjust")
    public Uni<Inventory> adjust(@PathParam("id") Long id, int adjustment) {
        return inventoryService.adjustQuantity(id, adjustment);
    }
}
