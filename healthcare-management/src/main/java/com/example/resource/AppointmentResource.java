package com.example.resource;

import com.example.entity.Appointment;
import com.example.service.AppointmentService;
import io.smallrye.mutiny.Uni;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/appointments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed({"PATIENT", "DOCTOR", "ADMIN"}) // Accessible by authenticated roles
public class AppointmentResource {
    private final AppointmentService appointmentService;

    @Inject
    public AppointmentResource(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @GET
    public Uni<List<Appointment>> getAll() {
        return appointmentService.listAll();
    }

    @GET
    @Path("/{id}")
    public Uni<Appointment> getById(@PathParam("id") Long id) {
        return appointmentService.findById(id);
    }

    @POST
    public Uni<Appointment> create(Appointment appointment) {
        return appointmentService.create(appointment);
    }

    @PUT
    @Path("/{id}")
    public Uni<Appointment> updateById(@PathParam("id") Long id, Appointment appointment) {
        return appointmentService.update(id, appointment);
    }

    @DELETE
    @Path("/{id}")
    public Uni<Void> deleteById(@PathParam("id") Long id) {
        return appointmentService.delete(id);
    }
}
