package com.healthcare.telemedicine.repository;

import com.healthcare.telemedicine.model.Consultation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConsultationRepository extends JpaRepository<Consultation, Long> {
    List<Consultation> findByStatus(Consultation.Status status);
    List<Consultation> findByPatientId(Long patientId);
    List<Consultation> findByDoctorId(Long doctorId);
}
