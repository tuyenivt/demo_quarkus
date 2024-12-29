package com.example.fhir;

import ca.uhn.fhir.rest.server.IResourceProvider;
import com.example.service.UserService;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.hl7.fhir.r4.model.ContactPoint;
import org.hl7.fhir.r4.model.Patient;

@Path("/fhir")
public class FHIRPatientResource implements IResourceProvider {
    private final UserService userService;

    @Inject
    public FHIRPatientResource(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Class<Patient> getResourceType() {
        return Patient.class;
    }

    @GET
    @Path("/Patient/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> getPatient(@PathParam("id") Long id) {
        return userService.findById(id)
                .onItem().ifNotNull().transform(user -> {
                    Patient patient = new Patient();
                    patient.setId(user.id.toString());
                    patient.addName().setFamily(user.fullName);
                    var contactPoint = new ContactPoint();
                    contactPoint.setSystem(ContactPoint.ContactPointSystem.EMAIL);
                    contactPoint.setValue(user.email);
                    patient.addTelecom(contactPoint);
                    // Add other FHIR patient details as needed
                    return Response.ok(patient).build();
                })
                .onItem().ifNull().continueWith(Response.status(Response.Status.NOT_FOUND).build());
    }
}
