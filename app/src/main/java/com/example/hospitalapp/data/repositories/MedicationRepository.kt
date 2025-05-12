package com.example.hospitalapp.data.repositories

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.hospitalapp.data.local.dao.MedicationDao
import com.example.hospitalapp.data.local.dao.UserDao
import com.example.hospitalapp.data.local.entities.MedicationEntity
import com.example.hospitalapp.data.local.extensions.toPatientResponse
import com.example.hospitalapp.network.model.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

interface MedicationRepository {
    suspend fun getMedications(): List<MedicationResponse>
    suspend fun getMedicationById(id: Long): MedicationResponse
    suspend fun getMedicationsByAppointment(appointmentId: Long): List<MedicationResponse>
    suspend fun getPatientMedications(patientId: Long): List<MedicationResponse>
    suspend fun getActiveMedications(patientId: Long): List<MedicationResponse>
    suspend fun createMedication(medication: MedicationRequest): MedicationResponse
    suspend fun updateMedication(id: Long, medication: MedicationRequest): MedicationResponse
    suspend fun deleteMedication(id: Long)
}

class MedicationRepositoryImpl (
    private val medicationDao: MedicationDao,
    private val userDao: UserDao
) : MedicationRepository {

    @RequiresApi(Build.VERSION_CODES.O)
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    private val currentDate = "2025-05-12 14:16:27" // Using the provided UTC time

    override suspend fun getMedications(): List<MedicationResponse> {
        return medicationDao.getAllMedications().map { it.toMedicationResponse() }
    }

    override suspend fun getMedicationById(id: Long): MedicationResponse {
        return medicationDao.getMedicationById(id)?.toMedicationResponse()
            ?: throw IllegalStateException("Medication not found")
    }

    override suspend fun getMedicationsByAppointment(appointmentId: Long): List<MedicationResponse> {
        return medicationDao.getMedicationsByAppointment(appointmentId).map { it.toMedicationResponse() }
    }

    override suspend fun getPatientMedications(patientId: Long): List<MedicationResponse> {
        return medicationDao.getPatientMedications(patientId).map { it.toMedicationResponse() }
    }

    override suspend fun getActiveMedications(patientId: Long): List<MedicationResponse> {
        return medicationDao.getActiveMedications(patientId).map { it.toMedicationResponse() }
    }

    override suspend fun createMedication(medication: MedicationRequest): MedicationResponse {
        val medicationEntity = MedicationEntity(
            patientId = medication.patientId,
            appointmentId = medication.appointmentId,
            name = medication.name,
            dosage = medication.dosage,
            frequency = medication.frequency,
            startDate = medication.startDate,
            endDate = medication.endDate,
            instructions = medication.instructions,
            active = true,
            createdAt = currentDate,
            updatedAt = currentDate
        )

        val id = medicationDao.insertMedication(medicationEntity)
        return getMedicationById(id)
    }

    override suspend fun updateMedication(id: Long, medication: MedicationRequest): MedicationResponse {
        val existing = medicationDao.getMedicationById(id)
            ?: throw IllegalStateException("Medication not found")

        val updatedMedication = existing.copy(
            name = medication.name,
            dosage = medication.dosage,
            frequency = medication.frequency,
            startDate = medication.startDate,
            endDate = medication.endDate,
            instructions = medication.instructions,
            updatedAt = currentDate
        )

        medicationDao.updateMedication(updatedMedication)
        return getMedicationById(id)
    }

    override suspend fun deleteMedication(id: Long) {
        val medication = medicationDao.getMedicationById(id)
            ?: throw IllegalStateException("Medication not found")
        medicationDao.deleteMedication(medication)
    }

    private suspend fun MedicationEntity.toMedicationResponse(): MedicationResponse {
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
}