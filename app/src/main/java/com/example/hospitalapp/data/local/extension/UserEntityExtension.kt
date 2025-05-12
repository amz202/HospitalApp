package com.example.hospitalapp.data.local.extensions

import com.example.hospitalapp.data.local.dao.AppointmentDao
import com.example.hospitalapp.data.local.dao.FeedbackDao
import com.example.hospitalapp.data.local.dao.MedicationDao
import com.example.hospitalapp.data.local.dao.UserDao
import com.example.hospitalapp.data.local.dao.VitalsDao
import com.example.hospitalapp.data.local.entities.*
import com.example.hospitalapp.network.model.*

// User extensions - Single implementation for each conversion
fun UserEntity.toUserResponse(): UserResponse {
    return UserResponse(
        id = id,
        username = username,
        email = email,
        fName = fName,
        lName = lName,
        phoneNumber = phoneNumber,
        gender = gender,
        dob = dob,
        address = address,
        roles = setOf(role),
        accountCreationDate = accountCreationDate
    )
}

fun UserEntity.toPatientResponse(): PatientResponse {
    return PatientResponse(
        id = id,
        username = username,
        email = email,
        fName = fName,
        lName = lName,
        phoneNumber = phoneNumber,
        gender = gender,
        dob = dob,
        address = address,
        role = role,
        accountCreationDate = accountCreationDate,
        bloodGroup = null,
        allergies = emptyList(),
        medicalHistory = emptyList()
    )
}

fun UserEntity.toDoctorResponse(): DoctorResponse {
    return DoctorResponse(
        id = id,
        username = username,
        email = email,
        fName = fName,
        lName = lName,
        phoneNumber = phoneNumber,
        gender = gender,
        dob = dob,
        address = address,
        role = role,
        accountCreationDate = accountCreationDate,
        specialization = "",
        qualification = "",
        experience = 0,
        availableForEmergency = false
    )
}


// Vitals extensions
suspend fun VitalsEntity.toVitalsResponse(userDao: UserDao): VitalsResponse {
    val patient = userDao.getUserById(patientId)?.toPatientResponse()
        ?: throw IllegalStateException("Patient not found")

    return VitalsResponse(
        id = id,
        patient = patient,
        heartRate = heartRate,
        systolicPressure = systolicPressure,
        diastolicPressure = diastolicPressure,
        temperature = temperature,
        oxygenSaturation = oxygenSaturation,
        respiratoryRate = respiratoryRate,
        bloodSugar = bloodSugar,
        recordedAt = recordedAt,
        critical = critical,
        criticalNotes = criticalNotes,
        alertSent = alertSent
    )
}

// Appointment extensions
suspend fun AppointmentEntity.toAppointmentResponse(userDao: UserDao): AppointmentResponse {
    val patient = userDao.getUserById(patientId)?.toPatientResponse()
        ?: throw IllegalStateException("Patient not found")
    val doctor = userDao.getUserById(doctorId)?.toDoctorResponse()
        ?: throw IllegalStateException("Doctor not found")

    return AppointmentResponse(
        id = id,
        patient = patient,
        doctor = doctor,
        scheduledTime = scheduledTime,
        status = AppointmentStatus.fromString(status),
        type = type,
        notes = notes,
        reason = reason,
        meetingLink = meetingLink,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

// Feedback extensions
suspend fun FeedbackEntity.toFeedbackResponse(
    userDao: UserDao,
    appointmentDao: AppointmentDao
): FeedbackResponse {
    val doctor = userDao.getUserById(doctorId)?.toDoctorResponse()
        ?: throw IllegalStateException("Doctor not found")
    val patient = userDao.getUserById(patientId)?.toPatientResponse()
        ?: throw IllegalStateException("Patient not found")
    val appointment = appointmentDao.getAppointmentById(appointmentId)?.toAppointmentResponse(userDao)
        ?: throw IllegalStateException("Appointment not found")

    return FeedbackResponse(
        id = id,
        comments = comments,
        diagnosis = diagnosis,
        recommendations = recommendations,
        nextSteps = nextSteps,
        createdAt = createdAt,
        updatedAt = updatedAt,
        doctor = doctor,
        patient = patient,
        appointment = appointment
    )
}

// Medication extensions
suspend fun MedicationEntity.toMedicationResponse(userDao: UserDao): MedicationResponse {
    val patient = userDao.getUserById(patientId)?.toPatientResponse()
        ?: throw IllegalStateException("Patient not found")

    return MedicationResponse(
        id = id,
        patient = patient,
        appointmentId = appointmentId,
        name = name,
        dosage = dosage,
        frequency = frequency,
        startDate = startDate,
        endDate = endDate,
        instructions = instructions,
        active = active,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

suspend fun ReportEntity.toReportResponse(
    userDao: UserDao,
    vitalsDao: VitalsDao,
    medicationDao: MedicationDao,
    feedbackDao: FeedbackDao,
    appointmentDao: AppointmentDao
): ReportResponse {
    val patient = userDao.getUserById(patientId)?.toPatientResponse()
        ?: throw IllegalStateException("Patient not found")

    val doctor = doctorId?.let {
        userDao.getUserById(it)?.toDoctorResponse()
    }

    val vitals = vitalsId?.let {
        vitalsDao.getVitalsById(it)?.toVitalsResponse(userDao)
    }

    val appointment = appointmentId?.let {
        appointmentDao.getAppointmentById(it)?.toAppointmentResponse(userDao)
    }

    val feedback = feedbackId?.let {
        feedbackDao.getFeedbackById(it)?.toFeedbackResponse(userDao, appointmentDao)
    }

    val medications = appointmentId?.let {
        medicationDao.getMedicationsByAppointment(it).map { med ->
            med.toMedicationResponse(userDao)
        }
    } ?: emptyList()

    return ReportResponse(
        id = id,
        title = title,
        generatedAt = generatedAt,
        patient = patient,
        doctor = doctor,
        summary = summary,
        reportType = reportType,
        vitals = vitals,
        medications = medications,
        feedback = feedback,
        appointment = appointment,
        filePath = filePath,
        timePeriodStart = timePeriodStart,
        timePeriodEnd = timePeriodEnd
    )
}