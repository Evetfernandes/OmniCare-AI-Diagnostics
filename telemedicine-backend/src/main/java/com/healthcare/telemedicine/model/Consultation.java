package com.healthcare.telemedicine.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "consultations")
@Data
@NoArgsConstructor
public class Consultation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private User patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id")
    private User doctor; // Can be null until a doctor accepts

    @Column(columnDefinition = "TEXT", nullable = false)
    private String symptoms;

    @Column(name = "ai_severity")
    private String aiSeverity;

    @Column(name = "ai_department")
    private String aiDepartment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.WAITING;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum Status {
        WAITING, IN_PROGRESS, COMPLETED, CANCELLED
    }
}
