package com.healthcare.telemedicine.dto;

import lombok.Data;

@Data
public class DiagnosticRequest {
    private String symptoms;
    private Integer heartRate;
    private String bloodPressure;
    private Double temperature;
    private String language;
}
