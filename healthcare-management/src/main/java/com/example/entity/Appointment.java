package com.example.entity;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.time.LocalDateTime;

@Entity
public class Appointment extends PanacheEntity {
    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    public User patient;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    public User doctor;

    public LocalDateTime appointmentTime;

    public String status; // e.g., "SCHEDULED", "CANCELLED", "COMPLETED"

    public String notes;
}
