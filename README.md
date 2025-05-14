# HospitalApp

HospitalApp is a healthcare management Android application developed using Kotlin, Jetpack Compose, and Room Database. It enables interaction between patients and doctors, allowing users to manage health data, medications, appointments, and reports in a fully offline environment.

---

## Key Features

### For Patients
- Health Dashboard: View vitals, medications, appointments, and summaries
- Appointment Management: Schedule, view, and manage upcoming appointments
- Medication Tracking: Monitor current and past medications with dosage details
- Health Reports: Generate and view detailed health summaries
- Emergency Access: Quick trigger for emergency alert functionality

### For Doctors
- Patient Overview: View assigned patients with complete medical details
- Vitals Recording: Record and monitor patients’ vital signs
- Prescription Management: Prescribe and manage medications per appointment
- Appointment Handling: View and manage doctor-specific appointments
- Feedback System: Access feedback from patients after consultations

### For Admins
- User Directory: View all doctors and patients in the system
- Login Simulation: Tap on any user to simulate logging into their account and view their dashboard

---

## Technical Overview

- Language: Kotlin
- UI: Jetpack Compose (Material 3)
- Architecture: MVVM
- Database: Room (no remote API used)
- Data Flow: ViewModels using StateFlow and LiveData
- Navigation: Jetpack Navigation with argument-based routing
- Local Persistence: Fully offline access via Room DAOs
- Session Management: Simulated login flow based on Room data

---

## Project Modules

| Layer        | Description                                      |
|--------------|--------------------------------------------------|
| `entities`   | Room entities for users, vitals, appointments, etc. |
| `dao`        | Data access interfaces for Room database         |
| `repository` | Business logic layer for ViewModels              |
| `viewmodels` | Screen-specific state management and logic       |
| `ui`         | Compose-based screens and navigation flow        |

---

## Usage Scenarios

- Patients can view and manage their complete health records
- Doctors can handle assigned patients, record vitals, and prescribe medication
- Admins can access any user account to view their dashboard data directly

---

## License

This project is built for educational and demonstration purposes only.
