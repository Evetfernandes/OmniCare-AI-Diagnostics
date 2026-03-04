import React, { useState, useEffect } from 'react';
import { Activity, ShieldHalf, Stethoscope, FileText, Database, Loader2, ArrowRightLeft, CheckCircle2, AlertTriangle, ExternalLink, Lightbulb, MapPin, Pill, Phone, Mic, MicOff, Download, Camera, Image as ImageIcon, X, Globe } from 'lucide-react';
import html2pdf from 'html2pdf.js';

const Navbar = ({ currentUser, toggleRole }) => (
    <nav className="bg-medical-900 text-white shadow-md p-4 flex justify-between items-center sticky top-0 z-50">
        <div className="font-bold text-xl flex items-center gap-2 tracking-wide">
            <Activity className="h-6 w-6 text-emerald-400" />
            OmniCare AI Diagnostics
        </div>
        <div className="flex gap-6 items-center">
            <button className="hover:text-emerald-300 transition font-medium hidden sm:block">Research DB</button>
            <button className="bg-white text-medical-900 px-5 py-2 rounded-full font-bold hover:bg-gray-100 transition shadow-sm">Logout</button>
        </div>
    </nav>
);

function App() {
    const [currentUser, setCurrentUser] = useState({
        id: 'user-' + Math.floor(Math.random() * 1000),
        name: 'Jane Doe',
        role: 'PATIENT'
    });

    const [symptoms, setSymptoms] = useState('');
    const [triageResult, setTriageResult] = useState(null);
    const [isSubmitting, setIsSubmitting] = useState(false);

    // Vitals State
    const [heartRate, setHeartRate] = useState('');
    const [bloodPressure, setBloodPressure] = useState('');
    const [temperature, setTemperature] = useState('');

    // Voice State
    const [isListening, setIsListening] = useState(false);
    const [interimTranscript, setInterimTranscript] = useState('');

    // Image Upload State
    const [imageFile, setImageFile] = useState(null);
    const [imagePreviewUrl, setImagePreviewUrl] = useState(null);

    // Language Translation State
    const [selectedLanguage, setSelectedLanguage] = useState('en-US');

    // Speech Recognition Setup
    const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
    const recognition = SpeechRecognition ? new SpeechRecognition() : null;

    useEffect(() => {
        if (recognition) {
            recognition.continuous = true;
            recognition.interimResults = true;
            recognition.lang = selectedLanguage;

            recognition.onresult = (event) => {
                let currentInterim = '';
                let newFinal = '';

                for (let i = event.resultIndex; i < event.results.length; i++) {
                    const transcript = event.results[i][0].transcript;
                    if (event.results[i].isFinal) {
                        newFinal += transcript + ' ';
                    } else {
                        currentInterim += transcript;
                    }
                }

                if (newFinal) {
                    setSymptoms(prev => prev + newFinal);
                }
                setInterimTranscript(currentInterim);
            };

            recognition.onerror = (event) => {
                console.error("Speech recognition error:", event.error);
                setIsListening(false);
            };

            recognition.onend = () => {
                setIsListening(false);
            };
        }
    }, [recognition, selectedLanguage]);

    const toggleListening = () => {
        if (!recognition) {
            alert("Your browser does not support the Web Speech API.");
            return;
        }

        if (isListening) {
            recognition.stop();
        } else {
            recognition.start();
            setIsListening(true);
        }
    };

    const downloadPDF = () => {
        const element = document.getElementById('diagnostic-report-content');
        if (!element) return;
        const opt = {
            margin: 0.5,
            filename: 'omnicare-diagnostic-report.pdf',
            image: { type: 'jpeg', quality: 0.98 },
            html2canvas: { scale: 2, useCORS: true },
            jsPDF: { unit: 'in', format: 'letter', orientation: 'portrait' }
        };
        html2pdf().set(opt).from(element).save();
    };

    const toggleRole = () => {
        setCurrentUser(prev => ({
            ...prev,
            role: prev.role === 'PATIENT' ? 'DOCTOR' : 'PATIENT'
        }));
        setTriageResult(null);
        setSymptoms('');
    };

    const handleImageChange = (e) => {
        const file = e.target.files[0];
        if (file && file.type.startsWith('image/')) {
            setImageFile(file);
            setImagePreviewUrl(URL.createObjectURL(file));
            setSymptoms(''); // Clear text when focusing on image
        }
    };

    const removeImage = () => {
        setImageFile(null);
        if (imagePreviewUrl) {
            URL.revokeObjectURL(imagePreviewUrl);
        }
        setImagePreviewUrl(null);
    };

    const handleSymptomSubmit = async (e) => {
        e.preventDefault();
        setIsSubmitting(true);

        try {
            let response;

            // If an image is uploaded, use the new multipart Computer Vision endpoint
            if (imageFile) {
                const formData = new FormData();
                formData.append('image', imageFile);

                response = await fetch('http://localhost:8080/api/diagnose/image', {
                    method: 'POST',
                    body: formData
                });
            } else {
                // Stick to the normal text-based symptom triage engine
                const payload = {
                    symptoms,
                    language: selectedLanguage
                };
                if (heartRate) payload.heartRate = parseInt(heartRate);
                if (bloodPressure) payload.bloodPressure = bloodPressure;
                if (temperature) payload.temperature = parseFloat(temperature);

                response = await fetch('http://localhost:8080/api/diagnose', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(payload)
                });
            }

            if (response.ok) {
                const data = await response.json();
                console.log("Diagnostic Data Received:", data);

                setTriageResult({
                    severity: data.severity || 'UNKNOWN',
                    department: data.department || 'General Medicine',
                    recommendation: data.recommendation || 'Please consult a doctor.',
                    conditions: data.conditions || [],
                    researchCitations: data.researchCitations || [],
                    datasets: data.datasets || [],
                    actionableLinks: data.actionableLinks || [],
                    isVisionAnalysis: data.isVisionAnalysis || false,
                    translationUsed: data.translationUsed || false
                });
            } else {
                console.error('Diagnostic API returned an error:', response.status);
            }
        } catch (error) {
            console.error('Failed to fetch from Diagnostic API', error);
        } finally {
            setIsSubmitting(false);
        }
    };

    const renderDiagnosticReport = () => {
        if (!triageResult) return null;

        const isEmergency = triageResult.severity === 'EMERGENCY';

        return (
            <div className="mt-12 animate-in fade-in slide-in-from-bottom-8 duration-700">

                {/* PDF Download Bar */}
                <div className="flex justify-end mb-4">
                    <button onClick={downloadPDF} className="group relative flex items-center gap-2 bg-gradient-to-r from-purple-600 to-indigo-600 hover:from-purple-700 hover:to-indigo-700 text-white font-bold py-3 px-6 rounded-2xl shadow-lg hover:shadow-xl transition-all hover:-translate-y-1">
                        <Download className="w-5 h-5 group-hover:animate-bounce" />
                        <span>Download PDF Report</span>
                    </button>
                </div>

                <div id="diagnostic-report-content" className="bg-white rounded-3xl shadow-xl border border-gray-100 overflow-hidden">
                    <div className={`p-8 ${isEmergency ? 'bg-red-600 text-white' : 'bg-medical-900 border-b border-medical-700 text-white'} flex justify-between items-center`}>
                        <div className="flex items-center gap-4">
                            {isEmergency ? <AlertTriangle className="w-10 h-10 animate-pulse text-red-100" /> : <ShieldHalf className="w-10 h-10 text-emerald-300" />}
                            <div>
                                <h2 className="text-3xl font-extrabold tracking-tight">AI Diagnostic Report</h2>
                                <p className="text-sm font-medium opacity-80 mt-1">Generated instantaneously via simulated clinical rules engine</p>
                            </div>
                        </div>
                        <div className={`px-5 py-2 text-lg font-black rounded-xl shadow-inner ${isEmergency ? 'bg-white text-red-700' : 'bg-medical-950 border border-medical-700 text-emerald-400'}`}>
                            {triageResult.severity}
                        </div>
                    </div>

                    <div className="p-8 space-y-8">

                        {/* Conditions section */}
                        <div>
                            <div className="flex flex-col md:flex-row md:items-center justify-between border-b pb-2 mb-4 gap-2">
                                <h3 className="text-xl font-bold text-gray-900 flex items-center gap-2">
                                    <Activity className="text-emerald-500" /> Detected Conditions
                                </h3>
                                <div className="flex gap-2 flex-wrap">
                                    {triageResult.translationUsed && (
                                        <span className="bg-blue-100 text-blue-700 text-xs font-bold px-3 py-1 rounded-full flex items-center gap-1 shadow-sm">
                                            <Globe className="w-3 h-3" /> Auto-Translated
                                        </span>
                                    )}
                                    {triageResult.isVisionAnalysis && (
                                        <span className="bg-purple-100 text-purple-700 text-xs font-bold px-3 py-1 rounded-full flex items-center gap-1 shadow-sm">
                                            <ImageIcon className="w-3 h-3" /> AI Vision Source Override
                                        </span>
                                    )}
                                </div>
                            </div>
                            <div className="space-y-4">
                                {triageResult.conditions.map((cond, idx) => (
                                    <div key={idx} className="flex flex-col md:flex-row md:items-center justify-between p-5 bg-gray-50 rounded-xl border border-gray-100 shadow-sm relative overflow-hidden">
                                        <div className="absolute left-0 top-0 bottom-0 w-1.5 bg-emerald-500"></div>
                                        <div>
                                            <h4 className="text-lg font-bold text-gray-800 flex items-center gap-2">
                                                {cond.name}
                                                <span className="text-xs bg-gray-200 text-gray-600 px-2 py-1 rounded font-mono">ICD-10: {cond.icd10}</span>
                                            </h4>
                                        </div>
                                        <div className="mt-3 md:mt-0 flex items-center gap-4">
                                            <div className="w-48 bg-gray-200 rounded-full h-3">
                                                <div className={`h-3 rounded-full ${cond.confidence > 80 ? 'bg-emerald-500' : 'bg-yellow-500'}`} style={{ width: `${cond.confidence}%` }}></div>
                                            </div>
                                            <span className="font-bold text-gray-700 w-12 text-right">{cond.confidence}%</span>
                                        </div>
                                    </div>
                                ))}
                            </div>
                        </div>

                        {/* Recommendations */}
                        <div className="bg-blue-50 p-6 rounded-xl border border-blue-100">
                            <h3 className="text-lg font-bold text-blue-900 mb-2 flex items-center gap-2">
                                <Lightbulb className="text-blue-500" /> Primary Recommendation
                            </h3>
                            <p className="text-blue-800 text-lg leading-relaxed">{triageResult.recommendation}</p>
                            <div className="mt-4 pt-4 border-t border-blue-200/50 flex gap-2 items-center text-sm font-bold text-blue-700">
                                Routed to Department: <span className="bg-white px-3 py-1 rounded-full text-blue-900 shadow-sm border border-blue-100">{triageResult.department}</span>
                            </div>
                        </div>

                        {/* Research & Datasets */}
                        <div className="grid md:grid-cols-2 gap-6 pt-4">

                            {/* Papers */}
                            <div className="bg-gray-50 p-6 rounded-xl border border-gray-200">
                                <h3 className="text-lg font-bold text-gray-800 mb-4 flex items-center gap-2">
                                    <FileText className="text-purple-500" /> Supporting Research
                                </h3>
                                <ul className="space-y-4 text-sm">
                                    {triageResult.researchCitations.map((paper, idx) => (
                                        <li key={idx} className="bg-white p-3 rounded-lg border border-gray-100 shadow-sm hover:shadow-md transition">
                                            <p className="font-semibold text-gray-800 leading-tight mb-1">{paper.title}</p>
                                            <div className="flex justify-between items-end">
                                                <span className="text-gray-500 text-xs italic">{paper.source}, {paper.year}</span>
                                                <a href={paper.url} target="_blank" rel="noreferrer" className="text-purple-600 flex items-center gap-1 hover:underline text-xs font-bold">
                                                    PubMed <ExternalLink className="w-3 h-3" />
                                                </a>
                                            </div>
                                        </li>
                                    ))}
                                </ul>
                            </div>

                            {/* Datasets */}
                            <div className="bg-gray-50 p-6 rounded-xl border border-gray-200">
                                <h3 className="text-lg font-bold text-gray-800 mb-4 flex items-center gap-2">
                                    <Database className="text-teal-500" /> Correlated Datasets
                                </h3>
                                <ul className="space-y-4">
                                    {triageResult.datasets.map((data, idx) => (
                                        <li key={idx} className="bg-white p-4 rounded-lg border border-gray-100 shadow-sm">
                                            <h4 className="font-bold justify-between text-gray-800 flex items-center gap-2 mb-2">
                                                {data.name} <CheckCircle2 className="w-4 h-4 text-teal-500" />
                                            </h4>
                                            <p className="text-sm text-gray-600 leading-relaxed">{data.description}</p>
                                        </li>
                                    ))}
                                </ul>
                            </div>
                        </div>

                        {/* Actionable Links */}
                        {triageResult.actionableLinks && triageResult.actionableLinks.length > 0 && (
                            <div className="bg-emerald-50 p-6 rounded-xl border border-emerald-100 mt-6">
                                <h3 className="text-lg font-bold text-emerald-900 mb-4 flex items-center gap-2">
                                    <MapPin className="text-emerald-500" /> Actionable Resources
                                </h3>
                                <div className="grid sm:grid-cols-2 gap-4">
                                    {triageResult.actionableLinks.map((link, idx) => (
                                        <a key={idx} href={link.url} target="_blank" rel="noreferrer"
                                            className="flex items-center gap-3 bg-white p-4 rounded-xl shadow-sm border border-emerald-100 hover:shadow-md hover:border-emerald-300 transition group">
                                            <div className="bg-emerald-100 p-2 rounded-lg text-emerald-600 group-hover:bg-emerald-500 group-hover:text-white transition">
                                                {link.type === 'MAPS' ? <MapPin className="w-5 h-5" /> : link.type === 'PHARMACY' ? <Pill className="w-5 h-5" /> : <Phone className="w-5 h-5" />}
                                            </div>
                                            <span className="font-bold text-gray-800">{link.label}</span>
                                            <ExternalLink className="w-4 h-4 text-gray-400 ml-auto group-hover:text-emerald-500 transition" />
                                        </a>
                                    ))}
                                </div>
                            </div>
                        )}

                    </div>
                </div>
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-gray-50 flex flex-col font-sans">
            <Navbar currentUser={currentUser} toggleRole={toggleRole} />

            <main className="flex-grow p-4 md:p-8">
                <div className="max-w-6xl mx-auto">

                    <div className="flex flex-col-reverse md:flex-row items-stretch justify-between gap-10 mb-8 mt-4">
                        <div className="flex-1">
                            <h1 className="text-4xl md:text-5xl font-extrabold text-gray-900 mb-6 tracking-tight leading-tight">
                                Clinical Precision, <br />
                                <span className="text-transparent bg-clip-text bg-gradient-to-r from-medical-500 to-medical-800">Driven by Data.</span>
                            </h1>
                            <p className="text-xl text-gray-600 mb-8 max-w-lg">
                                Input patient symptoms to receive an instant, evidence-based diagnostic analysis powered by simulated ML models and clinical datasets.
                            </p>

                            <div className="flex flex-wrap justify-start gap-4 text-sm font-medium text-gray-600 mb-6 bg-white p-4 rounded-xl shadow-sm border border-gray-100">
                                <div className="flex items-center gap-2"><div className="w-2 h-2 rounded-full bg-emerald-500"></div> User: <span className="font-bold text-gray-900">{currentUser.name}</span></div>
                                <div className="flex items-center gap-2"><div className="w-2 h-2 rounded-full bg-blue-500"></div> Role: <span className="font-bold text-gray-900">{currentUser.role}</span></div>
                            </div>

                            <button onClick={toggleRole} className="flex items-center gap-2 text-medical-700 hover:text-medical-900 font-bold transition group bg-medical-50 hover:bg-medical-100 px-4 py-2 rounded-lg border border-medical-200 shadow-sm">
                                <ArrowRightLeft className="w-5 h-5 group-hover:rotate-180 transition duration-500" />
                                Toggle View ({currentUser.role === 'PATIENT' ? 'Doctor View' : 'Patient View'})
                            </button>

                        </div>

                        <div className="md:w-[400px] flex items-center justify-center p-6 bg-white rounded-3xl shadow-xl border border-gray-100">
                            <div className="text-center">
                                <Database className="w-20 h-20 text-medical-200 mx-auto mb-4" />
                                <h3 className="font-bold text-gray-800 text-lg mb-2">Simulated Data Sources</h3>
                                <div className="flex flex-wrap justify-center gap-2">
                                    <span className="text-xs font-bold bg-gray-100 text-gray-600 px-2 py-1 rounded">MIMIC-IV</span>
                                    <span className="text-xs font-bold bg-gray-100 text-gray-600 px-2 py-1 rounded">PubMed Guidelines</span>
                                    <span className="text-xs font-bold bg-gray-100 text-gray-600 px-2 py-1 rounded">ICD-10 Registry</span>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div className="max-w-4xl mx-auto mt-12">
                        <div className="bg-white p-6 md:p-10 rounded-3xl shadow-xl border border-gray-100 relative overflow-hidden">
                            <div className="absolute top-0 left-0 w-full h-2 bg-gradient-to-r from-emerald-400 to-medical-600"></div>
                            <h2 className="text-2xl font-bold text-gray-800 mb-2 flex items-center gap-3">
                                <div className="bg-medical-50 p-3 rounded-2xl"><Activity className="text-emerald-500 w-6 h-6" /></div>
                                Diagnostic Input Engine
                            </h2>
                            <p className="text-gray-500 mb-8 text-sm md:text-base font-medium">Test keywords like: "severe chest pain", "headache nausea", or "runny nose".</p>

                            <form onSubmit={handleSymptomSubmit}>
                                {/* Vitals Section */}
                                <div className="mb-8">
                                    <h3 className="text-sm font-bold text-gray-700 mb-3 flex items-center gap-2"><Activity className="w-4 h-4 text-emerald-500" /> Patient Vitals <span className="text-xs text-gray-400 font-normal">(Optional)</span></h3>
                                    <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                                        <div className="bg-white border-2 border-rose-100 rounded-2xl p-4 shadow-sm hover:shadow-md transition focus-within:ring-2 focus-within:ring-rose-400 focus-within:border-rose-400">
                                            <label className="text-xs font-bold text-rose-500 uppercase tracking-wider flex items-center gap-1 mb-2">Heart Rate</label>
                                            <div className="flex items-end gap-2">
                                                <input type="number" placeholder="0" value={heartRate} onChange={e => setHeartRate(e.target.value)} className="w-full bg-transparent outline-none font-black text-2xl text-gray-800" />
                                                <span className="text-sm font-bold text-gray-400 mb-1">BPM</span>
                                            </div>
                                        </div>
                                        <div className="bg-white border-2 border-blue-100 rounded-2xl p-4 shadow-sm hover:shadow-md transition focus-within:ring-2 focus-within:ring-blue-400 focus-within:border-blue-400">
                                            <label className="text-xs font-bold text-blue-500 uppercase tracking-wider flex items-center gap-1 mb-2">Blood Pressure</label>
                                            <div className="flex items-end gap-2">
                                                <input type="text" placeholder="120/80" value={bloodPressure} onChange={e => setBloodPressure(e.target.value)} className="w-full bg-transparent outline-none font-black text-2xl text-gray-800" />
                                                <span className="text-sm font-bold text-gray-400 mb-1">mmHg</span>
                                            </div>
                                        </div>
                                        <div className="bg-white border-2 border-orange-100 rounded-2xl p-4 shadow-sm hover:shadow-md transition focus-within:ring-2 focus-within:ring-orange-400 focus-within:border-orange-400">
                                            <label className="text-xs font-bold text-orange-500 uppercase tracking-wider flex items-center gap-1 mb-2">Temperature</label>
                                            <div className="flex items-end gap-2">
                                                <input type="number" step="0.1" placeholder="98.6" value={temperature} onChange={e => setTemperature(e.target.value)} className="w-full bg-transparent outline-none font-black text-2xl text-gray-800" />
                                                <span className="text-sm font-bold text-gray-400 mb-1">°F</span>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                <div className="mb-8">
                                    <h3 className="text-sm font-bold text-gray-700 mb-3 flex items-center gap-2"><ImageIcon className="w-4 h-4 text-purple-500" /> AI Vision Scan <span className="text-xs text-gray-400 font-normal">(Optional)</span></h3>

                                    {!imagePreviewUrl ? (
                                        <div className="relative border-2 border-dashed border-gray-300 rounded-3xl p-8 hover:bg-gray-50 transition text-center focus-within:ring-4 focus-within:ring-purple-500/20 focus-within:border-purple-500">
                                            <input
                                                type="file"
                                                accept="image/*"
                                                onChange={handleImageChange}
                                                className="absolute inset-0 w-full h-full opacity-0 cursor-pointer"
                                                title="Upload Medical Image"
                                            />
                                            <Camera className="w-12 h-12 text-gray-400 mx-auto mb-4" />
                                            <p className="text-gray-600 font-medium">Drag and drop a photo here, or <span className="text-purple-600 font-bold hover:underline">browse files</span></p>
                                            <p className="text-xs text-gray-400 mt-2">Dermatology (Moles/Rashes) or ENT (Throat/Tonsils)</p>
                                        </div>
                                    ) : (
                                        <div className="relative border-2 border-purple-200 rounded-3xl p-2 bg-purple-50">
                                            <button
                                                type="button"
                                                onClick={removeImage}
                                                className="absolute -top-3 -right-3 bg-red-500 hover:bg-red-600 text-white p-2 rounded-full shadow-lg transition z-10 hover:scale-110"
                                            >
                                                <X className="w-4 h-4" />
                                            </button>
                                            <img src={imagePreviewUrl} alt="Medical scan preview" className="w-24 h-24 object-cover rounded-2xl shadow-sm border border-purple-100" />
                                            <div>
                                                <h4 className="font-bold text-gray-800 text-lg flex items-center gap-2"><CheckCircle2 className="w-5 h-5 text-emerald-500" /> Image Ready for AI Scan</h4>
                                                <p className="text-sm text-gray-600">The Computer Vision model will override text symptoms.</p>
                                                <p className="text-xs font-mono text-gray-400 mt-1 truncate max-w-[200px]">{imageFile?.name}</p>
                                            </div>
                                        </div>
                                    )}
                                </div>

                                <div className={`relative mb-8 transition-opacity duration-500 ${imageFile ? 'opacity-40 pointer-events-none' : 'opacity-100'}`}>
                                    <div className="flex flex-col md:flex-row md:items-center justify-between mb-3 gap-3">
                                        <h3 className="text-sm font-bold text-gray-700 flex items-center gap-2"><FileText className="w-4 h-4 text-emerald-500" /> Clinical Symptoms {!imageFile && <span className="text-red-500">*</span>}</h3>

                                        {/* Language Selector */}
                                        <div className="flex items-center gap-2 bg-gray-50 border border-gray-200 rounded-lg px-3 py-1.5 focus-within:ring-2 focus-within:ring-emerald-500/20 focus-within:border-emerald-500 transition">
                                            <Globe className="w-4 h-4 text-gray-500" />
                                            <select
                                                value={selectedLanguage}
                                                onChange={(e) => setSelectedLanguage(e.target.value)}
                                                className="bg-transparent text-sm font-bold text-gray-700 outline-none cursor-pointer appearance-none pr-4"
                                            >
                                                <option value="en-US">English</option>
                                                <option value="es-ES">Spanish (Español)</option>
                                                <option value="fr-FR">French (Français)</option>
                                                <option value="hi-IN">Hindi (हिंदी)</option>
                                                <option value="mr-IN">Marathi (मराठी)</option>
                                            </select>
                                        </div>
                                    </div>

                                    <div className="relative group">
                                        <textarea
                                            className="w-full border-2 border-gray-200 rounded-3xl p-6 pr-16 focus:ring-4 focus:ring-emerald-500/20 focus:border-emerald-500 transition text-lg bg-gray-50 focus:bg-white outline-none resize-none shadow-inner"
                                            rows="4"
                                            placeholder="Dictate or type patient symptoms here (e.g., severe chest pain radiating to the left arm with shortness of breath)..."
                                            value={symptoms + (interimTranscript ? (symptoms.length > 0 && !symptoms.endsWith(' ') ? ' ' : '') + interimTranscript : '')}
                                            onChange={(e) => setSymptoms(e.target.value)}
                                            required={!imageFile}
                                        ></textarea>
                                        <button
                                            type="button"
                                            onClick={toggleListening}
                                            className={`absolute right-4 bottom-4 p-4 rounded-full shadow-lg transition-all duration-300 flex items-center justify-center ${isListening
                                                ? 'bg-red-500 text-white animate-pulse shadow-red-500/50 scale-110'
                                                : 'bg-white text-gray-400 hover:text-emerald-600 hover:bg-emerald-50 border border-gray-100 hover:scale-105'
                                                }`}
                                            title="Dictate Symptoms"
                                        >
                                            {isListening ? <Mic className="w-6 h-6" /> : <MicOff className="w-6 h-6" />}
                                        </button>
                                        {isListening && (
                                            <div className="absolute top-4 right-6 text-xs font-bold text-red-500 animate-pulse flex items-center gap-1">
                                                <div className="w-2 h-2 bg-red-500 rounded-full"></div> Recording...
                                            </div>
                                        )}
                                    </div>
                                </div>


                                <button
                                    type="submit"
                                    disabled={isSubmitting || (!symptoms && !imageFile)}
                                    className={`w-full bg-gradient-to-r from-medical-700 to-medical-900 text-white font-extrabold py-5 px-6 rounded-3xl text-xl transition-all duration-300 flex justify-center items-center gap-3 shadow-xl ${isSubmitting || (!symptoms && !imageFile)
                                        ? 'opacity-60 cursor-not-allowed'
                                        : 'hover:shadow-2xl hover:shadow-medical-500/30 hover:-translate-y-1'
                                        }`}
                                >
                                    {isSubmitting ? (
                                        <><Loader2 className="animate-spin w-6 h-6" /> Processing Diagnostic Models...</>
                                    ) : (
                                        <><Activity className="w-6 h-6" /> Generate AI Diagnosis</>
                                    )}
                                </button>
                            </form>
                        </div>
                    </div>

                    {renderDiagnosticReport()}
                </div>
            </main>

            <footer className="bg-gray-900 text-gray-400 py-10 text-center mt-12 border-t border-gray-800">
                <p className="font-medium text-lg">&copy; 2026 OmniCare AI Diagnostics. Master's Capstone Project.</p>
            </footer>
        </div>
    );
}

// Quick component mock since I forgot to import Search
function Search(props) {
    return <svg {...props} xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><circle cx="11" cy="11" r="8" /><path d="m21 21-4.3-4.3" /></svg>
}

export default App;
