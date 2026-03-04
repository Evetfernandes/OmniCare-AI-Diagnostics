package com.healthcare.telemedicine.service;

import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class AiTriageService {

    /**
     * Simulates an AI/ML Engine determining the severity of symptoms.
     * In a full thesis project, this would call an external Python/FastAPI service wrapping a model.
     */
    public Map<String, String> analyzeSymptoms(String symptoms) {
        String s = symptoms.toLowerCase();
        Map<String, String> result = new HashMap<>();

        if (s.contains("chest") || s.contains("heart") || s.contains("breath") || s.contains("severe")) {
            result.put("severity", "EMERGENCY");
            result.put("department", "Cardiology / ER");
        } else if (s.contains("headache") || s.contains("migraine") || s.contains("vision")) {
            result.put("severity", "URGENT");
            result.put("department", "Neurology");
        } else if (s.contains("rash") || s.contains("skin")) {
            result.put("severity", "ROUTINE");
            result.put("department", "Dermatology");
        } else {
            result.put("severity", "ROUTINE");
            result.put("department", "General Practice");
        }

        return result;
    }
}
