package com.chatsystem.resource;

import com.chatsystem.model.User;
import com.chatsystem.service.UserService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

import java.util.List;

@Path("/api/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {
    
    private static final Logger LOG = Logger.getLogger(UserResource.class);
    
    @Inject
    UserService userService;
    
    @POST
    @Path("/register")
    public Response registerUser(UserRegistrationRequest request) {
        try {
            User user = userService.createUser(
                request.getUsername(),
                request.getPassword(),
                request.getEmail()
            );
            return Response.ok(user).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse(e.getMessage()))
                .build();
        } catch (Exception e) {
            LOG.error("Error registering user: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ErrorResponse("Internal server error"))
                .build();
        }
    }
    
    @GET
    @Path("/online")
    public List<User> getOnlineUsers() {
        return userService.getOnlineUsers();
    }
    
    @GET
    @Path("/search")
    public List<User> searchUsers(@QueryParam("q") String query) {
        return userService.searchUsers(query);
    }
    
    @GET
    @Path("/{username}")
    public Response getUser(@PathParam("username") String username) {
        User user = userService.findByUsername(username);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(new ErrorResponse("User not found"))
                .build();
        }
        return Response.ok(user).build();
    }
    
    @DELETE
    @Path("/{username}")
    public Response deleteUser(@PathParam("username") String username) {
        try {
            userService.deleteUser(username);
            return Response.ok().build();
        } catch (Exception e) {
            LOG.error("Error deleting user: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ErrorResponse("Internal server error"))
                .build();
        }
    }
    
    // Inner classes for request/response objects
    public static class UserRegistrationRequest {
        private String username;
        private String password;
        private String email;
        
        // Getters and setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }
    
    public static class ErrorResponse {
        private String message;
        
        public ErrorResponse(String message) {
            this.message = message;
        }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
} 