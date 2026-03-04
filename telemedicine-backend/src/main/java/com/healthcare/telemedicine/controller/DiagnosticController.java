package com.healthcare.telemedicine.controller;

import com.healthcare.telemedicine.dto.DiagnosticRequest;
import com.healthcare.telemedicine.service.DiagnosticService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/diagnose")
@CrossOrigin(origins = "*")
public class DiagnosticController {

    private final DiagnosticService diagnosticService;

    public DiagnosticController(DiagnosticService diagnosticService) {
        this.diagnosticService = diagnosticService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> diagnose(@RequestBody DiagnosticRequest request) {
        Map<String, Object> result = diagnosticService.analyzeSymptoms(request);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/image")
    public ResponseEntity<Map<String, Object>> diagnoseImage(
            @RequestParam("image") org.springframework.web.multipart.MultipartFile image) {
        try {
            Map<String, Object> result = diagnosticService.analyzeImage(image);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "severity", "ERROR",
                    "department", "IT/Support",
                    "recommendation", "Failed to process image: " + e.getMessage()));
        }
    }
}
