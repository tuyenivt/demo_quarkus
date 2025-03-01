package com.example.resource;

import com.example.entity.User;
import com.example.service.UserService;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {
    private final UserService userService;

    @Inject
    public UserResource(UserService userService) {
        this.userService = userService;
    }

    @GET
    public Uni<List<User>> getAll() {
        return userService.listAll();
    }

    @GET
    @Path("/{id}")
    public Uni<User> getById(@PathParam("id") Long id) {
        return userService.findById(id);
    }

    @POST
    public Uni<User> create(User user) {
        return userService.create(user);
    }

    @PUT
    @Path("/{id}")
    public Uni<User> updateById(@PathParam("id") Long id, User user) {
        return userService.update(id, user);
    }

    @DELETE
    @Path("/{id}")
    public Uni<Void> deleteById(@PathParam("id") Long id) {
        return userService.delete(id);
    }
}
