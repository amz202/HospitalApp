package com.example.hospitalapp.data.repositories

import com.example.hospitalapp.data.local.dao.*
import com.example.hospitalapp.data.local.entities.DoctorDetailEntity
import com.example.hospitalapp.data.local.entities.UserEntity
import com.example.hospitalapp.data.local.extensions.*
import com.example.hospitalapp.network.model.*
import javax.inject.Inject

interface DoctorRepository {
    suspend fun getDoctors(): List<DoctorResponse>
    suspend fun getDoctorById(id: Long): DoctorResponse
    suspend fun createDoctor(doctor: DoctorRequest): DoctorResponse
    suspend fun updateDoctor(id: Long, doctor: DoctorRequest): DoctorResponse
    suspend fun deleteDoctor(id: Long)
    suspend fun getDoctorPatients(id: Long): List<PatientResponse>
    suspend fun getDoctorsBySpecialization(specialization: String): List<DoctorResponse>
    suspend fun getDoctorAppointments(id: Long): List<AppointmentResponse>
    suspend fun prescribeMedication(
        doctorId: Long,
        appointmentId: Long,
        medication: MedicationRequest
    ): MedicationResponse
    suspend fun provideFeedback(
        doctorId: Long,
        appointmentId: Long,
        feedback: FeedbackRequest
    ): FeedbackResponse
}

class DoctorRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val doctorDetailDao: DoctorDetailDao,
    private val appointmentDao: AppointmentDao,
    private val medicationDao: MedicationDao,
    private val feedbackDao: FeedbackDao
) : DoctorRepository {

    override suspend fun getDoctors(): List<DoctorResponse> {
        return userDao.getUsersByRole("DOCTOR").mapNotNull { user ->
            val details = doctorDetailDao.getDoctorDetailByUserId(user.id)
            if (details != null) {
                DoctorResponse(
                    id = user.id,
                    username = user.username,
                    email = user.email,
                    fName = user.fName,
                    lName = user.lName,
                    phoneNumber = user.phoneNumber,
                    gender = user.gender,
                    dob = user.dob,
                    address = user.address,
                    role = user.role,
                    accountCreationDate = user.accountCreationDate,
                    specialization = details.specialization,
                    qualification = details.qualification,
                    experience = details.experienceYears,
                    availableForEmergency = details.availableForEmergency
                )
            } else null
        }
    }

    override suspend fun getDoctorById(id: Long): DoctorResponse {
        val user = userDao.getUserById(id)
            ?: throw IllegalStateException("Doctor not found")

        if (user.role != "DOCTOR") {
            throw IllegalStateException("User is not a doctor")
        }

        val details = doctorDetailDao.getDoctorDetailByUserId(id)
            ?: throw IllegalStateException("Doctor details not found")

        return DoctorResponse(
            id = user.id,
            username = user.username,
            email = user.email,
            fName = user.fName,
            lName = user.lName,
            phoneNumber = user.phoneNumber,
            gender = user.gender,
            dob = user.dob,
            address = user.address,
            role = user.role,
            accountCreationDate = user.accountCreationDate,
            specialization = details.specialization,
            qualification = details.qualification,
            experience = details.experienceYears,
            availableForEmergency = details.availableForEmergency
        )
    }

    override suspend fun createDoctor(doctor: DoctorRequest): DoctorResponse {
        if (userDao.getUserByEmail(doctor.email) != null) {
            throw IllegalStateException("Email already exists")
        }

        val currentDate = "2025-05-12 15:12:44"

        val userEntity = UserEntity(
            username = doctor.email.substringBefore("@"),
            password = doctor.password,
            email = doctor.email,
            fName = doctor.fName,
            lName = doctor.lName,
            phoneNumber = doctor.phoneNumber,
            gender = "",
            dob = "",
            address = "",
            role = "DOCTOR",
            accountCreationDate = currentDate
        )

        val userId = userDao.insertUser(userEntity)

        val doctorDetail = DoctorDetailEntity(
            userId = userId,
            specialization = doctor.specialization,
            licenseNumber = doctor.licenseNumber,
            qualification = doctor.qualification,
            experienceYears = doctor.experienceYears,
            consultationFee = doctor.consultationFee,
            availableForEmergency = doctor.availableForEmergency,
            createdAt = currentDate,
            updatedAt = currentDate
        )

        doctorDetailDao.insertDoctorDetail(doctorDetail)
        return getDoctorById(userId)
    }

    override suspend fun updateDoctor(id: Long, doctor: DoctorRequest): DoctorResponse {
        val existingUser = userDao.getUserById(id)
            ?: throw IllegalStateException("Doctor not found")

        if (existingUser.role != "DOCTOR") {
            throw IllegalStateException("User is not a doctor")
        }

        userDao.getUserByEmail(doctor.email)?.let {
            if (it.id != id) throw IllegalStateException("Email already exists")
        }

        val currentDate = "2025-05-12 15:12:44"

        val updatedUser = existingUser.copy(
            email = doctor.email,
            fName = doctor.fName,
            lName = doctor.lName,
            phoneNumber = doctor.phoneNumber
        )
        userDao.updateUser(updatedUser)

        val existingDetails = doctorDetailDao.getDoctorDetailByUserId(id)
            ?: throw IllegalStateException("Doctor details not found")

        val updatedDetails = existingDetails.copy(
            specialization = doctor.specialization,
            licenseNumber = doctor.licenseNumber,
            qualification = doctor.qualification,
            experienceYears = doctor.experienceYears,
            consultationFee = doctor.consultationFee,
            availableForEmergency = doctor.availableForEmergency,
            updatedAt = currentDate
        )
        doctorDetailDao.updateDoctorDetail(updatedDetails)

        return getDoctorById(id)
    }

    override suspend fun deleteDoctor(id: Long) {
        val user = userDao.getUserById(id)
            ?: throw IllegalStateException("Doctor not found")

        if (user.role != "DOCTOR") {
            throw IllegalStateException("User is not a doctor")
        }

        userDao.deleteUser(user)
        // DoctorDetail will be deleted automatically due to CASCADE
    }

    override suspend fun getDoctorPatients(id: Long): List<PatientResponse> {
        // First verify the doctor exists
        getDoctorById(id)

        // Get patients from appointments
        return appointmentDao.getDoctorPatients(id).map { user ->
            PatientResponse(
                id = user.id,
                username = user.username,
                email = user.email,
                fName = user.fName,
                lName = user.lName,
                phoneNumber = user.phoneNumber ?: "",
                gender = user.gender,
                dob = user.dob,
                address = user.address,
                role = user.role,  // Get the actual role from the user entity
                accountCreationDate = user.accountCreationDate,
                bloodGroup = null,
                allergies = emptyList(),
                medicalHistory = emptyList()
            )
        }.distinct()
    }

    override suspend fun getDoctorsBySpecialization(specialization: String): List<DoctorResponse> {
        val doctorDetails = doctorDetailDao.getDoctorsBySpecialization(specialization)
        return doctorDetails.mapNotNull { details ->
            userDao.getUserById(details.userId)?.let { user ->
                DoctorResponse(
                    id = user.id,
                    username = user.username,
                    email = user.email,
                    fName = user.fName,
                    lName = user.lName,
                    phoneNumber = user.phoneNumber,
                    gender = user.gender,
                    dob = user.dob,
                    address = user.address,
                    role = user.role,
                    accountCreationDate = user.accountCreationDate,
                    specialization = details.specialization,
                    qualification = details.qualification,
                    experience = details.experienceYears,
                    availableForEmergency = details.availableForEmergency
                )
            }
        }
    }

    override suspend fun getDoctorAppointments(id: Long): List<AppointmentResponse> {
        return appointmentDao.getDoctorAppointments(id).map {
            it.toAppointmentResponse(userDao)
        }
    }

    override suspend fun prescribeMedication(
        doctorId: Long,
        appointmentId: Long,
        medication: MedicationRequest
    ): MedicationResponse {
        getDoctorById(doctorId)

        val appointment = appointmentDao.getAppointmentById(appointmentId)
            ?: throw IllegalStateException("Appointment not found")

        if (appointment.doctorId != doctorId) {
            throw IllegalStateException("Appointment does not belong to this doctor")
        }

        val currentDate = "2025-05-12 15:12:44"

        val medicationEntity = medication.toMedicationEntity(
            doctorId = doctorId,
            appointmentId = appointmentId,
            currentDate = currentDate
        )

        val id = medicationDao.insertMedication(medicationEntity)
        return medicationDao.getMedicationById(id)?.toMedicationResponse(userDao)
            ?: throw IllegalStateException("Failed to create medication")
    }

    override suspend fun provideFeedback(
        doctorId: Long,
        appointmentId: Long,
        feedback: FeedbackRequest
    ): FeedbackResponse {
        getDoctorById(doctorId)

        val appointment = appointmentDao.getAppointmentById(appointmentId)
            ?: throw IllegalStateException("Appointment not found")

        if (appointment.doctorId != doctorId) {
            throw IllegalStateException("Appointment does not belong to this doctor")
        }

        val currentDate = "2025-05-12 15:12:44"

        val feedbackEntity = feedback.toFeedbackEntity(
            doctorId = doctorId,
            appointmentId = appointmentId,
            currentDate = currentDate
        )

        val id = feedbackDao.insertFeedback(feedbackEntity)
        return feedbackDao.getFeedbackById(id)?.toFeedbackResponse(userDao, appointmentDao)
            ?: throw IllegalStateException("Failed to create feedback")
    }
}