package com.healthcare.telemedicine.controller;

import lombok.Data;

@Data
public class ConsultationRequest {
    private Long patientId;
    private String symptoms;
}
