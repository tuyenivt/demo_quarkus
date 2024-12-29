package com.example.service;

import com.example.entity.Appointment;
import com.example.entity.User;
import com.example.ws.NotificationWebSocket;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.util.List;

@ApplicationScoped
public class AppointmentService {
    private static final Logger LOG = Logger.getLogger(AppointmentService.class);

    private final ObjectMapper objectMapper;

    @Inject
    public AppointmentService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public Uni<List<Appointment>> listAll() {
        return Appointment.listAll();
    }

    public Uni<Appointment> findById(Long id) {
        return Appointment.findById(id);
    }

    public Uni<Appointment> create(Appointment appointment) {
        return User.<User>findById(appointment.patient.id)
                .chain(patient -> User.<User>findById(appointment.doctor.id)
                        .onItem().ifNotNull().invoke(doctor -> {
                            appointment.patient = patient;
                            appointment.doctor = doctor;
                        })
                )
                .chain(() -> appointment.persist().replaceWith(appointment))
                .invoke(this::broadcastUpdate);
    }

    public Uni<Appointment> update(Long id, Appointment updatedAppointment) {
        return Appointment.<Appointment>findById(id)
                .onItem().ifNotNull().invoke(existing -> {
                    existing.appointmentTime = updatedAppointment.appointmentTime;
                    existing.status = updatedAppointment.status;
                    existing.notes = updatedAppointment.notes;
                    // Update patient and doctor if provided
                    if (updatedAppointment.patient != null && updatedAppointment.patient.id != null) {
                        existing.patient = updatedAppointment.patient;
                    }
                    if (updatedAppointment.doctor != null && updatedAppointment.doctor.id != null) {
                        existing.doctor = updatedAppointment.doctor;
                    }
                })
                .invoke(this::broadcastUpdate);
    }

    public Uni<Void> delete(Long id) {
        return Appointment.deleteById(id)
                .invoke(() -> broadcastDeletion(id))
                .replaceWithVoid();
    }

    private void broadcastUpdate(Appointment appointment) {
        try {
            String message = objectMapper.writeValueAsString(appointment);
            NotificationWebSocket.broadcast(message);
        } catch (JsonProcessingException e) {
            LOG.error("Failed to broadcast inventory update", e);
        }
    }

    private void broadcastDeletion(Long id) {
        try {
            String message = "{\"deletedId\":" + id + "}";
            NotificationWebSocket.broadcast(message);
        } catch (Exception e) {
            LOG.error("Failed to broadcast inventory deletion", e);
        }
    }
}
