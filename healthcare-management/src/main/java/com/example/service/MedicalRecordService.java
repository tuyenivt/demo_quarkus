package com.example.service;

import com.example.entity.MedicalRecord;
import com.example.entity.User;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class MedicalRecordService {
    public Uni<List<MedicalRecord>> listAll() {
        return MedicalRecord.listAll();
    }

    public Uni<MedicalRecord> findById(Long id) {
        return MedicalRecord.findById(id);
    }

    public Uni<MedicalRecord> create(MedicalRecord medicalRecord) {
        return User.<User>findById(medicalRecord.patient.id)
                .onItem().ifNotNull().invoke(patient -> medicalRecord.patient = patient)
                .chain(() -> medicalRecord.persist().replaceWith(medicalRecord));
    }

    public Uni<MedicalRecord> update(Long id, MedicalRecord updatedMedicalRecord) {
        return MedicalRecord.<MedicalRecord>findById(id)
                .onItem().ifNotNull().invoke(existing -> {
                    existing.recordDate = updatedMedicalRecord.recordDate;
                    existing.diagnosis = updatedMedicalRecord.diagnosis;
                    existing.treatment = updatedMedicalRecord.treatment;
                    existing.prescription = updatedMedicalRecord.prescription;
                    // Update patient if provided
                    if (updatedMedicalRecord.patient != null && updatedMedicalRecord.patient.id != null) {
                        existing.patient = updatedMedicalRecord.patient;
                    }
                });
    }

    public Uni<Void> delete(Long id) {
        return MedicalRecord.deleteById(id).replaceWithVoid();
    }
}
