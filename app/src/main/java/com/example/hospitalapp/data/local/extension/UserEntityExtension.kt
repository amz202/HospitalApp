package com.example.hospitalapp.data.local.extensions

import com.example.hospitalapp.data.local.dao.AppointmentDao
import com.example.hospitalapp.data.local.dao.DoctorDetailDao
import com.example.hospitalapp.data.local.dao.FeedbackDao
import com.example.hospitalapp.data.local.dao.MedicationDao
import com.example.hospitalapp.data.local.dao.PatientDetailDao
import com.example.hospitalapp.data.local.dao.UserDao
import com.example.hospitalapp.data.local.dao.VitalsDao
import com.example.hospitalapp.data.local.entities.*
import com.example.hospitalapp.network.model.*

// Vitals extensions
suspend fun VitalsEntity.toVitalsResponse(
    userDao: UserDao,
    patientDetailDao: PatientDetailDao
): VitalsResponse {
    val user = userDao.getUserById(patientId)
        ?: throw IllegalStateException("Patient not found")
    val patientDetails = patientDetailDao.getPatientDetailByUserId(patientId)
    val patient = user.toPatientResponse(patientDetails)

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
suspend fun AppointmentEntity.toAppointmentResponse(
    userDao: UserDao,
    patientDetailDao: PatientDetailDao,
    doctorDetailDao: DoctorDetailDao
): AppointmentResponse {
    val patientUser = userDao.getUserById(patientId)
        ?: throw IllegalStateException("Patient not found")
    val doctorUser = userDao.getUserById(doctorId)
        ?: throw IllegalStateException("Doctor not found")

    val patientDetails = patientDetailDao.getPatientDetailByUserId(patientId)
    val doctorDetails = doctorDetailDao.getDoctorDetailByUserId(doctorId)

    val patient = patientUser.toPatientResponse(patientDetails)
    val doctor = doctorUser.toDoctorResponse(doctorDetails)

    return AppointmentResponse(
        id = id,
        patient = patient,
        doctor = doctor,
        scheduledTime = scheduledTime,
        status = AppointmentStatus.fromString(status),
        type = type,
        notes = notes,
        reason = reason,
        meetingLink = virtualMeetingUrl,
    )
}

// Feedback extensions
suspend fun FeedbackEntity.toFeedbackResponse(
    userDao: UserDao,
    patientDetailDao: PatientDetailDao,
    doctorDetailDao: DoctorDetailDao,
    appointmentDao: AppointmentDao
): FeedbackResponse {
    val doctorUser = userDao.getUserById(doctorId)
        ?: throw IllegalStateException("Doctor not found")
    val patientUser = userDao.getUserById(patientId)
        ?: throw IllegalStateException("Patient not found")

    val doctorDetails = doctorDetailDao.getDoctorDetailByUserId(doctorId)
    val patientDetails = patientDetailDao.getPatientDetailByUserId(patientId)

    val doctor = doctorUser.toDoctorResponse(doctorDetails)
    val patient = patientUser.toPatientResponse(patientDetails)
    val appointment = appointmentDao.getAppointmentById(appointmentId)
        ?.toAppointmentResponse(userDao, patientDetailDao, doctorDetailDao)
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
suspend fun MedicationEntity.toMedicationResponse(
    userDao: UserDao,
    patientDetailDao: PatientDetailDao
): MedicationResponse {
    val patientUser = userDao.getUserById(patientId)
        ?: throw IllegalStateException("Patient not found")
    val patientDetails = patientDetailDao.getPatientDetailByUserId(patientId)
    val patient = patientUser.toPatientResponse(patientDetails)

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

// Report extensions
suspend fun ReportEntity.toReportResponse(
    userDao: UserDao,
    patientDetailDao: PatientDetailDao,
    doctorDetailDao: DoctorDetailDao,
    vitalsDao: VitalsDao,
    medicationDao: MedicationDao,
    feedbackDao: FeedbackDao,
    appointmentDao: AppointmentDao
): ReportResponse {
    val patientUser = userDao.getUserById(patientId)
        ?: throw IllegalStateException("Patient not found")
    val patientDetails = patientDetailDao.getPatientDetailByUserId(patientId)
    val patient = patientUser.toPatientResponse(patientDetails)

    val doctor = doctorId?.let {
        val doctorUser = userDao.getUserById(it)
        val doctorDetails = doctorDetailDao.getDoctorDetailByUserId(it)
        doctorUser?.toDoctorResponse(doctorDetails)
    }

    val vitals = vitalsId?.let {
        vitalsDao.getVitalsById(it)?.toVitalsResponse(userDao, patientDetailDao)
    }

    val appointment = appointmentId?.let {
        appointmentDao.getAppointmentById(it)?.toAppointmentResponse(
            userDao,
            patientDetailDao,
            doctorDetailDao
        )
    }

    val feedback = feedbackId?.let {
        feedbackDao.getFeedbackById(it)?.toFeedbackResponse(
            userDao,
            patientDetailDao,
            doctorDetailDao,
            appointmentDao
        )
    }

    val medications = appointmentId?.let {
        medicationDao.getMedicationsByAppointment(it).map { med ->
            med.toMedicationResponse(userDao, patientDetailDao)
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

// Request to Entity conversions
fun MedicationRequest.toMedicationEntity(
    doctorId: Long,
    appointmentId: Long,
    currentDate: String
): MedicationEntity = MedicationEntity(
    patientId = patientId,
    appointmentId = appointmentId,
    name = name,
    dosage = dosage,
    frequency = frequency,
    startDate = startDate,
    endDate = endDate,
    instructions = instructions,
    active = true,
    createdAt = currentDate,
    updatedAt = currentDate
)

fun FeedbackRequest.toFeedbackEntity(
    doctorId: Long,
    appointmentId: Long,
    currentDate: String
): FeedbackEntity = FeedbackEntity(
    doctorId = doctorId,
    patientId = patientId,
    appointmentId = appointmentId,
    comments = comments,
    diagnosis = diagnosis,
    recommendations = recommendations,
    nextSteps = nextSteps,
    createdAt = currentDate,
    updatedAt = currentDate
)

// User entity extensions remain the same as they were already updated
fun UserEntity.toUserResponse(): UserResponse {
    return UserResponse(
        id = id,
        role = role,
        accountCreationDate = accountCreationDate
    )
}

fun UserEntity.toPatientResponse(details: PatientDetailEntity?): PatientResponse {
    return PatientResponse(
        id = id,
        role = role,
        accountCreationDate = accountCreationDate,
        bloodGroup = details?.bloodGroup,
        allergies = details?.allergies ?: emptyList(),
        medicalHistory = details?.medicalHistory ?: emptyList(),
        fName = details?.fName ?: "",
        lName = details?.lName ?: "",
        phoneNumber = details?.phoneNumber,
        gender = details?.gender ?: "",
        email = details?.email ?: "",
    )
}

fun UserEntity.toDoctorResponse(details: DoctorDetailEntity?): DoctorResponse {
    return DoctorResponse(
        id = id,
        role = role,
        accountCreationDate = accountCreationDate,
        specialization = details?.specialization ?: "",
        licenseNumber = details?.licenseNumber ?: "",
        qualification = details?.qualification ?: "",
        experienceYears = details?.experienceYears ?: 0,
        consultationFee = details?.consultationFee ?: 0.0,
        availableForEmergency = details?.availableForEmergency ?: false,
        fName = details?.fName ?: "",
        lName = details?.lName ?: ""
    )
}