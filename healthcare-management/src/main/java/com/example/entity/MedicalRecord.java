package com.example.entity;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.time.LocalDate;

@Entity
public class MedicalRecord extends PanacheEntity {
    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    public User patient;

    public LocalDate recordDate;

    public String diagnosis;

    public String treatment;

    public String prescription;
}
