# HospitalApp

HospitalApp is a healthcare management Android application developed using Kotlin, Jetpack Compose, and Room Database. It enables interaction between patients and doctors, allowing users to manage health data, medications, appointments, and reports in a fully offline environment.

---

## Demo Video

Watch the demo here: [Project Demo (Google Drive)](https://drive.google.com/drive/folders/1JqrRPiPh69ajVC1ynwyC4S5B_ScH0LQk?usp=sharing)

---

## GUI Screenshot

<table>
  <tr>
    <td>
      <img src="https://github.com/user-attachments/assets/af90c5bd-a929-4084-a5ca-bfbcaeda4094" width="300"/>
    </td>
    <td>
      <img src="https://github.com/user-attachments/assets/67f16c15-3a9c-4744-b3d8-a7f2530b52c3" width="300"/>
    </td>
  </tr>
</table>

---

## Installation Guide

Follow these simple steps to set up and run the project:

### 1. Install Android Studio
- Download from [https://developer.android.com/studio](https://developer.android.com/studio)
- Install all recommended components (SDK, Emulator, etc.)

### 2. Open the Project
- Clone the repository or download the ZIP
- Open it in Android Studio (`File > Open`)

### 3. Run on Emulator
- Go to `Tools > Device Manager` and create a virtual device
- Press the green Run button to launch the app on emulator

> The app uses a local Room database and runs completely offline.

---

## Patient Features
- View vitals, medications, appointments, and summaries
- Schedule and manage appointments
- Track current and past medications
- Generate health summaries
- Emergency alert functionality

## Doctor Features
- View assigned patients and their medical details
- Record vitals and prescribe medication
- Manage doctor-specific appointments
- View feedback from patients

## Admin Features
- View all users (doctors and patients)
- Simulate login to any account to access their dashboard

---

## Technical Overview

- Language: Kotlin
- UI: Jetpack Compose (Material 3)
- Architecture: MVVM
- Database: Room (Offline Only)
- Navigation: Jetpack Navigation with arguments
- Session: Simulated login via Room-stored users

---

## Project Structure

| Layer        | Description                                      |
|--------------|--------------------------------------------------|
| `entities`   | Room entities like users, vitals, appointments   |
| `dao`        | Data access interfaces for Room database         |
| `repository` | Business logic between DAO and ViewModels        |
| `viewmodels` | Screen-specific logic and state handling         |
| `ui`         | Compose screens and navigation                   |

---

## Usage Scenarios

- Patients manage health records
- Doctors handle appointments and prescriptions
- Admins simulate logins to access any user's dashboard

---

## License

This project is for educational use only.

