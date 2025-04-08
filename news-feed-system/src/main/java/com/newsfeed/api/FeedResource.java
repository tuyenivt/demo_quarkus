package com.newsfeed.api;

import com.newsfeed.model.UserInteraction;
import com.newsfeed.service.FeedService;
import com.newsfeed.service.UserService;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

@Path("/api/v1/feed")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FeedResource {
    private static final Logger LOG = Logger.getLogger(FeedResource.class);

    @Inject
    FeedService feedService;

    @Inject
    UserService userService;

    @GET
    @Path("/{userId}")
    public Uni<Response> getFeed(
            @PathParam("userId") Long userId,
            @QueryParam("limit") @DefaultValue("20") int limit) {
        return feedService.getPersonalizedFeed(userId, limit)
                .onItem().transform(feed -> Response.ok(feed).build())
                .onFailure().recoverWithItem(throwable -> {
                    LOG.errorf("Error getting feed for user %d: %s", userId, throwable.getMessage());
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                            .entity("Error retrieving feed")
                            .build();
                });
    }

    @POST
    @Path("/{userId}/interact")
    public Uni<Response> recordInteraction(
            @PathParam("userId") Long userId,
            UserInteraction interaction) {
        return Uni.createFrom().item(() -> {
            try {
                userService.recordInteraction(userId, interaction);
                return Response.ok().build();
            } catch (Exception e) {
                LOG.errorf("Error recording interaction for user %d: %s", userId, e.getMessage());
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("Error recording interaction")
                        .build();
            }
        });
    }

    @GET
    @Path("/health")
    public Response healthCheck() {
        return Response.ok().entity("News Feed System is healthy").build();
    }
} 