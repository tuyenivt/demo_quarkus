package com.example.resource;

import com.example.entity.MedicalRecord;
import com.example.service.MedicalRecordService;
import io.smallrye.mutiny.Uni;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/medical-records")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed({"DOCTOR", "ADMIN"}) // Only DOCTORS and ADMIN can manage medical records
public class MedicalRecordResource {
    private final MedicalRecordService medicalRecordService;

    @Inject
    public MedicalRecordResource(MedicalRecordService medicalRecordService) {
        this.medicalRecordService = medicalRecordService;
    }

    @GET
    public Uni<List<MedicalRecord>> getAll() {
        return medicalRecordService.listAll();
    }

    @GET
    @Path("/{id}")
    public Uni<MedicalRecord> getById(@PathParam("id") Long id) {
        return medicalRecordService.findById(id);
    }

    @POST
    public Uni<MedicalRecord> create(MedicalRecord medicalRecord) {
        return medicalRecordService.create(medicalRecord);
    }

    @PUT
    @Path("/{id}")
    public Uni<MedicalRecord> updateById(@PathParam("id") Long id, MedicalRecord medicalRecord) {
        return medicalRecordService.update(id, medicalRecord);
    }

    @DELETE
    @Path("/{id}")
    public Uni<Void> deleteById(@PathParam("id") Long id) {
        return medicalRecordService.delete(id);
    }
}
