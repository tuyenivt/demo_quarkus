package com.example.idgenerator.resource;

import com.example.idgenerator.service.UniqueIdGenerator;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.jboss.resteasy.reactive.RestQuery;

@Path("/api/v1/ids")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class IdGeneratorResource {

    @Inject
    UniqueIdGenerator idGenerator;

    @GET
    @Path("/generate")
    @Counted(name = "generateIdCount", description = "Number of single ID generation requests")
    @Timed(name = "generateIdTimer", description = "Time taken to generate a single ID")
    public Response generateId() {
        try {
            long id = idGenerator.generateId();
            return Response.ok(new IdResponse(id)).build();
        } catch (Exception e) {
            return Response.serverError()
                    .entity(new ErrorResponse("Failed to generate ID: " + e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/generate-batch")
    @Counted(name = "generateBatchCount", description = "Number of batch ID generation requests")
    @Timed(name = "generateBatchTimer", description = "Time taken to generate a batch of IDs")
    public Response generateBatch(@RestQuery @DefaultValue("1") int count) {
        try {
            if (count < 1 || count > 1000) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("Count must be between 1 and 1000"))
                        .build();
            }
            long[] ids = idGenerator.generateBatch(count);
            return Response.ok(new BatchIdResponse(ids)).build();
        } catch (Exception e) {
            return Response.serverError()
                    .entity(new ErrorResponse("Failed to generate batch IDs: " + e.getMessage()))
                    .build();
        }
    }

    public static class IdResponse {
        private final long id;

        public IdResponse(long id) {
            this.id = id;
        }

        public long getId() {
            return id;
        }
    }

    public static class BatchIdResponse {
        private final long[] ids;

        public BatchIdResponse(long[] ids) {
            this.ids = ids;
        }

        public long[] getIds() {
            return ids;
        }
    }

    public static class ErrorResponse {
        private final String error;

        public ErrorResponse(String error) {
            this.error = error;
        }

        public String getError() {
            return error;
        }
    }
} 