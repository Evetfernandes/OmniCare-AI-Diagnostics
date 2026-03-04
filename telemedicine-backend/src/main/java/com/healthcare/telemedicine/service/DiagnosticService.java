package com.healthcare.telemedicine.service;

import org.springframework.stereotype.Service;
import com.healthcare.telemedicine.dto.DiagnosticRequest;
import java.util.*;

@Service
public class DiagnosticService {

        private static class ConditionRule {
                List<String> keywords;
                int severityLevel; // 3 = EMERGENCY, 2 = URGENT, 1 = ROUTINE
                String severityName;
                String department;
                String recommendation;
                List<Map<String, Object>> conditions;
                List<Map<String, Object>> citations;
                List<Map<String, Object>> datasets;
                List<Map<String, Object>> actionableLinks;

                ConditionRule(List<String> keywords, int severityLevel, String severityName, String department,
                                String recommendation,
                                List<Map<String, Object>> conditions, List<Map<String, Object>> citations,
                                List<Map<String, Object>> datasets, List<Map<String, Object>> actionableLinks) {
                        this.keywords = keywords;
                        this.severityLevel = severityLevel;
                        this.severityName = severityName;
                        this.department = department;
                        this.recommendation = recommendation;
                        this.conditions = conditions;
                        this.citations = citations;
                        this.datasets = datasets;
                        this.actionableLinks = actionableLinks;
                }
        }

        private final List<ConditionRule> diseaseDatabase = new ArrayList<>();

        public DiagnosticService() {
                populateDatabase();
        }

        private void populateDatabase() {
                // 1. CARDIOLOGY (Heart)
                diseaseDatabase.add(new ConditionRule(
                                Arrays.asList("chest", "heart", "palpitation", "flutter", "angina", "cardiac"),
                                3, "EMERGENCY", "Cardiology / Emergency",
                                "Seek immediate emergency medical attention for severe chest pain or suspected heart issues. Call emergency services immediately.",
                                Arrays.asList(
                                                createCondition("Acute Coronary Syndrome (Myocardial Infarction)", 89.4,
                                                                "I21.9"),
                                                createCondition("Cardiac Arrhythmia", 60.1, "I49.9")),
                                Arrays.asList(
                                                createCitation("2021 AHA/ACC/ASE/CHEST Guideline", "Circulation", 2021,
                                                                "https://pubmed.ncbi.nlm.nih.gov/34709879/"),
                                                createCitation("Coronary Artery Calcium Scoring", "EHJ", 2020,
                                                                "https://pubmed.ncbi.nlm.nih.gov/32049323/")),
                                Arrays.asList(createDataset("MIMIC-IV Clinical Database",
                                                "Matched critical care parameters from 53k admissions.")),
                                Arrays.asList(
                                                createActionableLink("Find Nearby Emergency Rooms",
                                                                "https://www.google.com/maps/search/emergency+room+near+me",
                                                                "MAPS"),
                                                createActionableLink("Call 911", "tel:911", "PHONE"))));

                // 2. GASTROENTEROLOGY (Stomach, Intestines)
                diseaseDatabase.add(new ConditionRule(
                                Arrays.asList(
                                                // English
                                                "stomach", "vomit", "nausea", "belly", "abdomen", "diarrhea", "cramp",
                                                "bloat", "acid", "gas",
                                                // Spanish
                                                "estómago", "vómito", "náusea", "vientre", "abdomen", "diarrea",
                                                // French
                                                "estomac", "vomissement", "nausée", "ventre", "diarrhée",
                                                // Hindi
                                                "पेट", "उल्टी", "मतली", "दस्त", "ऐंठन",
                                                // Marathi
                                                "पोट", "उलट्या", "मळमळ", "जुलाब", "पेटके"),
                                2, "URGENT", "Gastroenterology",
                                "Stay hydrated and stick to a bland diet. Visit urgent care if pain is severe, radiating, or accompanied by high fever.",
                                Arrays.asList(
                                                createCondition("Acute Gastroenteritis", 85.2, "A09"),
                                                createCondition("Appendicitis", 45.4, "K35.80"),
                                                createCondition("Peptic Ulcer Disease", 30.1, "K27.9")),
                                Arrays.asList(createCitation("Clinical Guidelines for Infectious Gastroenteritis",
                                                "Gastroenterology", 2017, "https://pubmed.ncbi.nlm.nih.gov/28842144/")),
                                Arrays.asList(createDataset("CDC FoodNet",
                                                "Matched enteric infection patterns in the US.")),
                                Arrays.asList(
                                                createActionableLink("Find Nearby Urgent Care",
                                                                "https://www.google.com/maps/search/urgent+care+near+me",
                                                                "MAPS"),
                                                createActionableLink("Buy Oral Rehydration Salts",
                                                                "https://www.google.com/search?tbm=shop&q=oral+rehydration+salts",
                                                                "PHARMACY"))));

                // 3. NEUROLOGY / ENT (Brain, Head, Dizziness)
                diseaseDatabase.add(new ConditionRule(
                                Arrays.asList(
                                                // English
                                                "vertigo", "spinning", "lightheaded", "faint", "brain",
                                                "confusion", "slurred", "stroke", "concussion", "head",
                                                "aura", "migraine", "neurological", "headache",
                                                // Spanish
                                                "vértigo", "mareo", "cabeza", "confusión", "migraña", "dolor de cabeza",
                                                // French
                                                "vertige", "tournis", "tête", "confusion", "migraine", "mal de tête",
                                                // Hindi
                                                "चक्कर", "सिर", "भ्रम", "माइग्रेन", "सिरदर्द",
                                                // Marathi
                                                "चक्कर", "डोके", "गोंधळ", "मायग्रेन", "डोकेदुखी"),
                                3, "EMERGENCY", "Neurology / ENT",
                                "Sit or lie down strictly. If experiencing confusion, facial drooping, or slurred speech, seek emergency stroke care instantly. For standard migraines, rest in a dark quiet room.",
                                Arrays.asList(
                                                createCondition("Migraine with Aura", 62.1, "G43.1"),
                                                createCondition("Benign Paroxysmal Positional Vertigo", 55.1, "H81.1"),
                                                createCondition("Transient Ischemic Attack (TIA)", 40.5, "G45.9")),
                                Arrays.asList(createCitation("Acute Treatment of Migraine", "Headache", 2015,
                                                "https://pubmed.ncbi.nlm.nih.gov/25600718/")),
                                Arrays.asList(createDataset("NHANES Dataset",
                                                "Correlated vestibular symptom frequencies.")),
                                Arrays.asList(
                                                createActionableLink("Find Nearby Neurology Clinics",
                                                                "https://www.google.com/maps/search/neurology+clinic+near+me",
                                                                "MAPS"),
                                                createActionableLink("Buy Stroke Response Aspirin",
                                                                "https://www.google.com/search?tbm=shop&q=aspirin",
                                                                "PHARMACY"),
                                                createActionableLink("Buy Electrolyte Water",
                                                                "https://www.google.com/search?tbm=shop&q=electrolyte+water",
                                                                "PHARMACY"))));

                // 4. PULMONOLOGY (Lungs, Throat)
                diseaseDatabase.add(new ConditionRule(
                                Arrays.asList(
                                                // English
                                                "fever", "cough", "breath", "chill", "sore throat", "lung", "asthma",
                                                "wheeze", "shortness of breath", "phlegm", "bronchitis", "runny nose",
                                                // Spanish
                                                "fiebre", "tos", "respiración", "garganta", "pulmón", "asma",
                                                "goteo nasal",
                                                // French
                                                "fièvre", "toux", "respiration", "gorge", "poumon", "asthme",
                                                "nez qui coule",
                                                // Hindi
                                                "बुखार", "खांसी", "सांस", "गला", "फेफड़े", "अस्थमा", "बहती नाक",
                                                // Marathi
                                                "ताप", "खोकला", "श्वास", "घसा", "फुफ्फुस", "अस्थमा", "वाहते नाक"),
                                2, "URGENT", "Infectious Disease / Pulmonology",
                                "Isolate from others and rest. Seek immediate emergency care for severe shortness of breath or blue lips.",
                                Arrays.asList(createCondition("Influenza / Viral Infection", 81.3, "J11"),
                                                createCondition("COVID-19", 75.9, "U07.1"),
                                                createCondition("Asthma Exacerbation", 60.2, "J45.901")),
                                Arrays.asList(createCitation("Clinical Mgmt of Airborne Infections", "JAMA", 2020,
                                                "https://pubmed.ncbi.nlm.nih.gov/32902646/")),
                                Arrays.asList(createDataset("WHO GISRS", "Matched respiratory symptom combinations.")),
                                Arrays.asList(
                                                createActionableLink("Find Pharmacies",
                                                                "https://www.google.com/maps/search/pharmacies+near+me",
                                                                "MAPS"),
                                                createActionableLink("Buy Fever Reducers",
                                                                "https://www.google.com/search?tbm=shop&q=acetaminophen+tylenol",
                                                                "PHARMACY"),
                                                createActionableLink("Buy Asthma Inhaler (OTC/Rx)",
                                                                "https://www.google.com/search?tbm=shop&q=primatene+mist",
                                                                "PHARMACY"))));

                // 5. ORTHOPEDICS - SPINE (Back)
                diseaseDatabase.add(new ConditionRule(
                                Arrays.asList("back", "spine", "sciatica", "lumbar", "disc", "vertebra"),
                                1, "ROUTINE", "Orthopedics / Neurology",
                                "Avoid heavy lifting and use heat/ice therapy. Seek physical therapy if mobility is severely restricted.",
                                Arrays.asList(createCondition("Lumbar Disc Herniation", 76.8, "M51.2"),
                                                createCondition("Sciatica", 60.1, "M54.3")),
                                Arrays.asList(createCitation("Treatment of Low Back Pain", "BMJ", 2017,
                                                "https://www.ncbi.nlm.nih.gov/pmc/articles/PMC8102949/")),
                                Arrays.asList(createDataset("National Spine Registry",
                                                "Matched phenotypic symptom spread.")),
                                Arrays.asList(
                                                createActionableLink("Find Physical Therapists",
                                                                "https://www.google.com/maps/search/physical+therapy+near+me",
                                                                "MAPS"),
                                                createActionableLink("Buy Back Pain Patches",
                                                                "https://www.google.com/search?tbm=shop&q=lidocaine+patch",
                                                                "PHARMACY"))));

                // 6. DERMATOLOGY (Skin, Rashes)
                diseaseDatabase.add(new ConditionRule(
                                Arrays.asList("rash", "itch", "hives", "blister", "acne", "mole", "lesion"),
                                1, "ROUTINE", "Dermatology / Allergy",
                                "Apply over-the-counter hydrocortisone cream and take an antihistamine. Seek urgent care if accompanied by throat swelling.",
                                Arrays.asList(createCondition("Allergic Contact Dermatitis", 88.5, "L23.9"),
                                                createCondition("Urticaria (Hives)", 70.1, "L50.9")),
                                Arrays.asList(createCitation("Practice Parameter for Immunodeficiency", "JACI", 2015,
                                                "https://pubmed.ncbi.nlm.nih.gov/26371839/")),
                                Arrays.asList(createDataset("Derm101 Image Database",
                                                "Utilized CNN visual-symptom mapping.")),
                                Arrays.asList(
                                                createActionableLink("Find Pharmacies",
                                                                "https://www.google.com/maps/search/pharmacies+near+me",
                                                                "MAPS"),
                                                createActionableLink("Buy Antihistamine Cream",
                                                                "https://www.google.com/search?tbm=shop&q=hydrocortisone+cream",
                                                                "PHARMACY"))));

                // 7. ORTHOPEDICS - JOINTS (Knee, Joints)
                diseaseDatabase.add(new ConditionRule(
                                Arrays.asList("joint", "knee", "stiff", "arthritis", "cartilage"),
                                1, "ROUTINE", "Rheumatology / Orthopedics",
                                "Rest the affected joint. Use over-the-counter NSAIDs for inflammation.",
                                Arrays.asList(createCondition("Osteoarthritis", 82.2, "M19.9"),
                                                createCondition("Rheumatoid Arthritis", 55.4, "M06.9")),
                                Arrays.asList(createCitation("ACR Guidelines for RA", "Arthritis & Rheumatology", 2021,
                                                "https://pubmed.ncbi.nlm.nih.gov/34101376/")),
                                Arrays.asList(createDataset("Arthritis Foundation Registry",
                                                "Matched auto-immune inflammatory markers.")),
                                Arrays.asList(
                                                createActionableLink("Find Orthopedics",
                                                                "https://www.google.com/maps/search/orthopedic+clinic+near+me",
                                                                "MAPS"),
                                                createActionableLink("Buy Joint Support Brace",
                                                                "https://www.google.com/search?tbm=shop&q=joint+support+brace",
                                                                "PHARMACY"))));

                // 8. PSYCHIATRY (Anxiety)
                diseaseDatabase.add(new ConditionRule(
                                Arrays.asList("anxiety", "panic", "stress", "depression", "suicide", "mental",
                                                "nervous"),
                                2, "URGENT", "Psychiatry",
                                "Practice deep breathing exercises. If having dark thoughts, contact a local crisis hotline immediately.",
                                Arrays.asList(createCondition("Panic Disorder", 79.1, "F41.0"),
                                                createCondition("Major Depressive Disorder", 65.2, "F32.9")),
                                Arrays.asList(createCitation("Clinical Practice Guideline for PTSD", "APA Guidelines",
                                                2017, "https://www.apa.org/ptsd-guideline")),
                                Arrays.asList(createDataset("NIMH Mental Health Surveillance",
                                                "Correlated psychophysiological symptoms.")),
                                Arrays.asList(
                                                createActionableLink("Find Therapists",
                                                                "https://www.google.com/maps/search/therapists+near+me",
                                                                "MAPS"))));

                // 9. NEPHROLOGY / UROLOGY (Kidneys, Bladder)
                diseaseDatabase.add(new ConditionRule(
                                Arrays.asList("kidney", "urine", "pee", "burn", "flank", "bladder", "urinate",
                                                "blood in urine", "calculus", "stone"),
                                2, "URGENT", "Nephrology / Urology",
                                "Drink plenty of water to flush the urinary tract. Severe flank pain radiating to the groin with nausea may indicate kidney stones requiring an ER visit.",
                                Arrays.asList(
                                                createCondition("Urinary Tract Infection (UTI)", 88.0, "N39.0"),
                                                createCondition("Nephrolithiasis (Kidney Stones)", 65.5, "N20.0")),
                                Arrays.asList(createCitation("Management of Kidney Stones", "BMJ", 2018,
                                                "https://pubmed.ncbi.nlm.nih.gov/29475850/")),
                                Arrays.asList(createDataset("National Kidney Foundation Registry",
                                                "Correlated renal symptom vectors vs verified metabolic panels.")),
                                Arrays.asList(
                                                createActionableLink("Find Nearby Urology Clinics",
                                                                "https://www.google.com/maps/search/urologist+near+me",
                                                                "MAPS"),
                                                createActionableLink("Buy UTI Pain Relief",
                                                                "https://www.google.com/search?tbm=shop&q=azo+urinary+pain+relief",
                                                                "PHARMACY"))));

                // 10. HEPATOLOGY (Liver, Gallbladder)
                diseaseDatabase.add(new ConditionRule(
                                Arrays.asList("liver", "jaundice", "yellow", "gallbladder", "gallstone",
                                                "upper right abdomen", "dark urine"),
                                3, "EMERGENCY", "Hepatology / Gastroenterology",
                                "Yellowing of the skin or eyes (jaundice) or severe upper right quadrant pain after eating fatty foods requires immediate medical evaluation.",
                                Arrays.asList(
                                                createCondition("Cholelithiasis (Gallstones)", 77.2, "K80.20"),
                                                createCondition("Hepatitis", 50.4, "K75.9")),
                                Arrays.asList(createCitation("Diagnosis and Management of Gallstone Disease", "JAMA",
                                                2022, "https://pubmed.ncbi.nlm.nih.gov/35510619/")),
                                Arrays.asList(createDataset("United Network for Organ Sharing (UNOS)",
                                                "Mapped hepatobiliary distress markers.")),
                                Arrays.asList(
                                                createActionableLink("Find Gastroenterologists",
                                                                "https://www.google.com/maps/search/gastroenterologist+near+me",
                                                                "MAPS"),
                                                createActionableLink("Emergency Room Map",
                                                                "https://www.google.com/maps/search/emergency+room+near+me",
                                                                "MAPS"))));

                // 11. ENDOCRINOLOGY / INTERNAL (Pancreas, Thyroid, Neck)
                diseaseDatabase.add(new ConditionRule(
                                Arrays.asList("pancreas", "thyroid", "goiter", "diabetes", "thirsty", "endocrine"),
                                2, "URGENT", "Endocrinology / ENT",
                                "Extreme upper central abdominal pain radiating to the back may be acute pancreatitis (Go to ER). Lumps in the neck or swallowing issues require an ENT scan.",
                                Arrays.asList(
                                                createCondition("Acute Pancreatitis", 45.2, "K85.90"),
                                                createCondition("Hypothyroidism / Goiter", 55.1, "E03.9"),
                                                createCondition("Type 2 Diabetes Mellitus", 65.8, "E11.9")),
                                Arrays.asList(createCitation("Management of Acute Pancreatitis", "Gastroenterology",
                                                2018, "https://pubmed.ncbi.nlm.nih.gov/29409760/")),
                                Arrays.asList(createDataset("Endocrine Society Data",
                                                "Analyzed thyroid and pancreatic exocrine hormone deficiencies.")),
                                Arrays.asList(
                                                createActionableLink("Find Endocrinology Clinics",
                                                                "https://www.google.com/maps/search/endocrinologist+near+me",
                                                                "MAPS"),
                                                createActionableLink("Buy Blood Glucose Monitor",
                                                                "https://www.google.com/search?tbm=shop&q=blood+glucose+monitor",
                                                                "PHARMACY"))));

                // 12. ORTHOPEDICS - EXTREMITIES (Hands, Arms, Legs, Feet)
                diseaseDatabase.add(new ConditionRule(
                                Arrays.asList("hand", "wrist", "finger", "arm", "carpal", "leg", "calf",
                                                "foot", "ankle", "shin", "fracture", "broken"),
                                2, "URGENT", "Orthopedics / Vascular",
                                "If an extremity is deformed, extremely swollen, or cannot bear weight, get an X-Ray. Severe, sudden calf pain can be a DVT (blood clot)—seek ER care.",
                                Arrays.asList(
                                                createCondition("Carpal Tunnel Syndrome", 72.5, "G56.00"),
                                                createCondition("Deep Vein Thrombosis (DVT)", 55.2, "I82.40"),
                                                createCondition("Bone Fracture / Sprain", 80.1, "T14.2X")),
                                Arrays.asList(createCitation("Diagnosis of Deep Vein Thrombosis",
                                                "American Family Physician", 2023,
                                                "https://pubmed.ncbi.nlm.nih.gov/37053531/")),
                                Arrays.asList(createDataset("National Trauma Data Bank",
                                                "Aggregated extremity musculoskeletal and vascular acute incidences.")),
                                Arrays.asList(
                                                createActionableLink("Find Urgent Care / X-Ray",
                                                                "https://www.google.com/maps/search/urgent+care+x-ray+near+me",
                                                                "MAPS"),
                                                createActionableLink("Buy Wrist Brace",
                                                                "https://www.google.com/search?tbm=shop&q=carpal+tunnel+wrist+brace",
                                                                "PHARMACY"))));

                // 13. PROCTOLOGY / LOWER GI (Buttocks, Rectum, Pelvis)
                diseaseDatabase.add(new ConditionRule(
                                Arrays.asList("butt", "rectum", "stool", "hemorrhoid", "tailbone", "pelvis",
                                                "anal", "coccydynia"),
                                1, "ROUTINE", "Proctology / Gastroenterology",
                                "Bright red blood during bowel movements is often hemorrhoids. Use over-the-counter soothing creams. If blood is dark/black, see a doctor immediately.",
                                Arrays.asList(
                                                createCondition("Hemorrhoids", 89.2, "K64.9"),
                                                createCondition("Coccydynia (Tailbone Pain)", 60.1, "M53.3")),
                                Arrays.asList(createCitation(
                                                "Clinical Practice Guideline for the Management of Hemorrhoids",
                                                "Diseases of the Colon & Rectum", 2018,
                                                "https://pubmed.ncbi.nlm.nih.gov/29424756/")),
                                Arrays.asList(createDataset("US GI Health Database",
                                                "Symptom spread mapped to outpatient lower-GI procedures.")),
                                Arrays.asList(
                                                createActionableLink("Find Gastroenterology Clinics",
                                                                "https://www.google.com/maps/search/gastroenterologist+near+me",
                                                                "MAPS"),
                                                createActionableLink("Buy Hemorrhoid Cream (Preparation H)",
                                                                "https://www.google.com/search?tbm=shop&q=preparation+h",
                                                                "PHARMACY"),
                                                createActionableLink("Buy Donut Cushion",
                                                                "https://www.google.com/search?tbm=shop&q=donut+cushion+for+tailbone+pain",
                                                                "PHARMACY"))));
        }

        public Map<String, Object> analyzeSymptoms(DiagnosticRequest request) {
                String lowerSymptoms = request.getSymptoms().toLowerCase();

                // Simulate ML processing
                try {
                        Thread.sleep(600);
                } catch (InterruptedException ignored) {
                }

                int highestSeverityLevel = 0;
                String finalSeverityName = "ROUTINE";
                Set<String> departments = new LinkedHashSet<>();
                StringBuilder recommendations = new StringBuilder();

                List<Map<String, Object>> finalConditions = new ArrayList<>();
                List<Map<String, Object>> finalCitations = new ArrayList<>();
                List<Map<String, Object>> finalDatasets = new ArrayList<>();
                List<Map<String, Object>> finalActionableLinks = new ArrayList<>();

                boolean matchesFound = false;

                for (ConditionRule rule : diseaseDatabase) {
                        boolean ruleMatched = false;
                        for (String keyword : rule.keywords) {
                                if (lowerSymptoms.contains(keyword)) {
                                        ruleMatched = true;
                                        break;
                                }
                        }

                        if (ruleMatched) {
                                matchesFound = true;
                                if (rule.severityLevel > highestSeverityLevel) {
                                        highestSeverityLevel = rule.severityLevel;
                                        finalSeverityName = rule.severityName;
                                }

                                departments.add(rule.department);
                                if (recommendations.length() > 0)
                                        recommendations.append(" | ");
                                recommendations.append(rule.recommendation);

                                // Final conditions list builder for this rule
                                List<Map<String, Object>> adjustedConditions = new ArrayList<>();
                                for (Map<String, Object> condition : rule.conditions) {
                                        String name = (String) condition.get("name");
                                        double confidence = (Double) condition.get("confidence");

                                        // Apply Biometric Vitals Impact
                                        if (request.getTemperature() != null && request.getTemperature() > 100.4) {
                                                if (name.contains("Infection") || name.contains("COVID")
                                                                || name.contains("Gastroenteritis")
                                                                || name.contains("UTI")
                                                                || name.contains("Appendicitis")) {
                                                        confidence += 15.5; // High fever boosts infection confidence
                                                }
                                        }

                                        if (request.getHeartRate() != null && request.getHeartRate() > 100) {
                                                if (name.contains("Arrhythmia") || name.contains("Panic")
                                                                || name.contains("Anxiety") || name.contains("Coronary")
                                                                || name.contains("Asthma")) {
                                                        confidence += 12.2; // Tachycardia boosts heart/panic/breathing
                                                                            // issues
                                                }
                                        }

                                        if (request.getBloodPressure() != null) {
                                                // Simple parsing if format is e.g. "150/90"
                                                try {
                                                        int sys = Integer.parseInt(
                                                                        request.getBloodPressure().split("/")[0]
                                                                                        .trim());
                                                        if (sys > 140) {
                                                                if (name.contains("Ischemic") || name.contains("Stroke")
                                                                                || name.contains("Coronary")
                                                                                || name.contains("Myocardial")
                                                                                || name.contains("Kidney")) {
                                                                        confidence += 18.0; // High BP drastically
                                                                                            // boosts
                                                                                            // cardiovascular/renal risk
                                                                }
                                                        }
                                                } catch (Exception ignored) {
                                                }
                                        }

                                        // Cap at 99.9%
                                        confidence = Math.min(confidence, 99.9);

                                        // Ensure one decimal place display consistency roughly
                                        confidence = Math.round(confidence * 10.0) / 10.0;

                                        Map<String, Object> adj = new HashMap<>(condition);
                                        adj.put("confidence", confidence);
                                        adjustedConditions.add(adj);
                                }

                                finalConditions.addAll(adjustedConditions);
                                finalCitations.addAll(rule.citations);
                                finalDatasets.addAll(rule.datasets);
                                finalActionableLinks.addAll(rule.actionableLinks);
                        }
                }

                if (!matchesFound) {
                        // Default Match
                        departments.add("General Practice");
                        recommendations.append("Monitor symptoms and stay hydrated. Schedule a routine check-up.");
                        finalConditions.add(createCondition("Viral Upper Respiratory Infection", 60.5, "J06.9"));
                        finalCitations.add(createCitation("Clinical Practice Guideline: Adult Sinusitis",
                                        "Otolaryngology–Head and Neck Surgery", 2015,
                                        "https://pubmed.ncbi.nlm.nih.gov/25832968/"));
                        finalDatasets.add(createDataset("CDC National Ambulatory Medical Care Survey",
                                        "Symptom presentation correlates with the top 10% most common outpatient visit ICD-10 codes in the NAMCS dataset."));
                        finalActionableLinks.add(createActionableLink("Find Nearby Clinics",
                                        "https://www.google.com/maps/search/medical+clinics+near+me", "MAPS"));
                        finalActionableLinks.add(createActionableLink("Buy Vitamin C",
                                        "https://www.google.com/search?tbm=shop&q=vitamin+c+immune+support",
                                        "PHARMACY"));
                }

                // De-duplicate references
                String joinedDepartments = String.join(" & ", departments);

                Map<String, Object> response = new HashMap<>();
                response.put("severity", finalSeverityName);
                response.put("department", joinedDepartments);
                response.put("recommendation", recommendations.toString());
                response.put("conditions", deduplicateListMap(finalConditions, "name"));
                response.put("researchCitations", deduplicateListMap(finalCitations, "title"));

                // Add Multi-Lingual Translation Flag
                boolean translationUsed = request.getLanguage() != null
                                && !request.getLanguage().equalsIgnoreCase("en-US");
                response.put("translationUsed", translationUsed);

                response.put("datasets", deduplicateListMap(finalDatasets, "name"));
                response.put("actionableLinks", deduplicateListMap(finalActionableLinks, "label"));

                return response;
        }

        private List<Map<String, Object>> deduplicateListMap(List<Map<String, Object>> list, String uniqueKey) {
                Set<String> seen = new HashSet<>();
                List<Map<String, Object>> deduplicated = new ArrayList<>();
                for (Map<String, Object> map : list) {
                        String val = (String) map.get(uniqueKey);
                        if (val != null && !seen.contains(val)) {
                                seen.add(val);
                                deduplicated.add(map);
                        }
                }
                return deduplicated;
        }

        private Map<String, Object> createCondition(String name, double confidence, String icd10) {
                Map<String, Object> map = new HashMap<>();
                map.put("name", name);
                map.put("confidence", confidence);
                map.put("icd10", icd10);
                return map;
        }

        public Map<String, Object> analyzeImage(org.springframework.web.multipart.MultipartFile image) {
                // In a real application, this would pass the image bytes to a Vision API (e.g.,
                // AWS Rekognition, Google Cloud Vision, or a custom PyTorch model).
                // Since this is a capstone simulation, we mock the Computer Vision analysis
                // results based on the file name or a random distribution.

                String fileName = image.getOriginalFilename() != null ? image.getOriginalFilename().toLowerCase() : "";
                List<Map<String, Object>> simulatedConditions = new ArrayList<>();
                List<Map<String, Object>> simulatedCitations = new ArrayList<>();
                List<Map<String, Object>> simulatedDatasets = new ArrayList<>();
                List<Map<String, Object>> actionableLinks = new ArrayList<>();

                String severity = "ROUTINE";
                String department = "Dermatology";
                String recommendation = "Consult a dermatologist for a professional biopsy and skin examination.";

                // Simulate logic based on file name keywords or pseudo-randomness
                if (fileName.contains("throat") || fileName.contains("tonsil")) {
                        severity = "URGENT";
                        department = "ENT (Ear, Nose, Throat)";
                        recommendation = "Severe tonsillar inflammation detected. Recommend immediate strep test and possible antibiotics prescription.";
                        simulatedConditions.add(createCondition("Acute Tonsillitis with Exudate", 88.5, "J03.90"));
                        simulatedConditions.add(createCondition("Streptococcal Pharyngitis", 76.2, "J02.0"));

                        simulatedCitations.add(createCitation("Diagnosis and Management of Acute Pharyngitis",
                                        "American Family Physician", 2018,
                                        "https://pubmed.ncbi.nlm.nih.gov/29671549/"));
                        simulatedDatasets.add(createDataset("Stanford ENT Image Dataset",
                                        "Deep learning model trained on 10,000+ labeled oropharyngeal examinations."));
                        actionableLinks.add(createActionableLink("Find ENT Specialists",
                                        "https://www.google.com/maps/search/ENT+specialist+near+me", "MAPS"));

                } else if (fileName.contains("mole") || fileName.contains("melanoma") || fileName.contains("lesion")) {
                        severity = "URGENT";
                        department = "Oncology / Dermatology";
                        recommendation = "Asymmetrical borders and varied coloration detected. Urgent dermatological biopsy recommended to rule out melanoma.";
                        simulatedConditions.add(createCondition("Malignant Melanoma (Suspected)", 64.8, "C43.9"));
                        simulatedConditions.add(createCondition("Dysplastic Nevus", 52.1, "D22.9"));

                        simulatedCitations.add(createCitation("Dermoscopy in the Diagnosis of Melanoma", "JAAD", 2019,
                                        "https://pubmed.ncbi.nlm.nih.gov/30392764/"));
                        simulatedDatasets.add(createDataset("HAM10000 Dataset",
                                        "Multisource dermatoscopic images of common pigmented skin lesions."));
                        actionableLinks.add(createActionableLink("Find Dermatologist",
                                        "https://www.google.com/maps/search/dermatologist+near+me", "MAPS"));

                } else {
                        // Default simulated finding: Eczema/Rash
                        severity = "ROUTINE";
                        department = "Dermatology / General Practice";
                        recommendation = "Visual markers suggest contact dermatitis or a mild inflammatory skin condition. Apply hydrocortisone cream and monitor.";
                        simulatedConditions.add(createCondition("Contact Dermatitis", 92.1, "L23.9"));
                        simulatedConditions.add(createCondition("Psoriasis Plaque", 35.4, "L40.0"));

                        simulatedCitations.add(createCitation("Topical Therapies for Atopic Dermatitis",
                                        "JAMA Dermatology", 2021, "https://pubmed.ncbi.nlm.nih.gov/34260655/"));
                        simulatedDatasets.add(createDataset("DermNet NZ Database",
                                        "Clinical presentation matched against 500k standard dermatological cases."));
                        actionableLinks.add(createActionableLink("Buy Hydrocortisone Ointment",
                                        "https://www.google.com/search?tbm=shop&q=hydrocortisone+cream", "PHARMACY"));
                }

                Map<String, Object> response = new HashMap<>();
                response.put("severity", severity);
                response.put("department", department);
                response.put("recommendation", recommendation);
                response.put("conditions", deduplicateListMap(simulatedConditions, "name"));
                response.put("researchCitations", deduplicateListMap(simulatedCitations, "title"));
                response.put("datasets", deduplicateListMap(simulatedDatasets, "name"));
                response.put("actionableLinks", deduplicateListMap(actionableLinks, "label"));

                // Add a special flag to denote this is an IMAGE based diagnosis
                response.put("isVisionAnalysis", true);

                return response;
        }

        private Map<String, Object> createCitation(String title, String source, int year, String url) {
                return Map.of("title", title, "source", source, "year", year, "url", url);
        }

        private Map<String, Object> createDataset(String name, String description) {
                return Map.of("name", name, "description", description);
        }

        private Map<String, Object> createActionableLink(String label, String url, String type) {
                return Map.of("label", label, "url", url, "type", type);
        }
}
