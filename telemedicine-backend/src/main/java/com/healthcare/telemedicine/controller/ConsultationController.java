package com.healthcare.telemedicine.controller;

import com.healthcare.telemedicine.model.Consultation;
import com.healthcare.telemedicine.model.User;
import com.healthcare.telemedicine.repository.ConsultationRepository;
import com.healthcare.telemedicine.repository.UserRepository;
import com.healthcare.telemedicine.service.AiTriageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/consultations")
@CrossOrigin(origins = "*")
public class ConsultationController {

    @Autowired
    private ConsultationRepository consultationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AiTriageService aiTriageService;

    // Patient requests a new consultation
    @PostMapping("/request")
    public ResponseEntity<?> requestConsultation(@RequestBody ConsultationRequest request) {
        Optional<User> patientOpt = userRepository.findById(request.getPatientId());
        
        if (patientOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Error: Patient not found");
        }

        Consultation consultation = new Consultation();
        consultation.setPatient(patientOpt.get());
        consultation.setSymptoms(request.getSymptoms());
        
        // Pass through AI Engine
        Map<String, String> triageResult = aiTriageService.analyzeSymptoms(request.getSymptoms());
        consultation.setAiSeverity(triageResult.get("severity"));
        consultation.setAiDepartment(triageResult.get("department"));
        
        Consultation savedConsultation = consultationRepository.save(consultation);

        return ResponseEntity.ok(savedConsultation);
    }

    // Doctor checks waiting room
    @GetMapping("/waiting")
    public ResponseEntity<?> getWaitingConsultations() {
        List<Consultation> waiting = consultationRepository.findByStatus(Consultation.Status.WAITING);
        return ResponseEntity.ok(waiting);
    }

    // Doctor accepts a consultation
    @PostMapping("/{id}/accept")
    public ResponseEntity<?> acceptConsultation(@PathVariable Long id, @RequestParam Long doctorId) {
        Optional<Consultation> consultationOpt = consultationRepository.findById(id);
        Optional<User> doctorOpt = userRepository.findById(doctorId);

        if (consultationOpt.isEmpty() || doctorOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Error: Invalid consultation or doctor ID");
        }

        Consultation consultation = consultationOpt.get();
        if (consultation.getStatus() != Consultation.Status.WAITING) {
            return ResponseEntity.badRequest().body("Error: Consultation is no longer waiting");
        }

        consultation.setDoctor(doctorOpt.get());
        consultation.setStatus(Consultation.Status.IN_PROGRESS);
        
        Consultation updated = consultationRepository.save(consultation);
        return ResponseEntity.ok(updated);
    }

    // End a consultation
    @PostMapping("/{id}/complete")
    public ResponseEntity<?> completeConsultation(@PathVariable Long id) {
        Optional<Consultation> consultationOpt = consultationRepository.findById(id);
        if (consultationOpt.isPresent()) {
            Consultation consultation = consultationOpt.get();
            consultation.setStatus(Consultation.Status.COMPLETED);
            return ResponseEntity.ok(consultationRepository.save(consultation));
        }
        return ResponseEntity.notFound().build();
    }
}
