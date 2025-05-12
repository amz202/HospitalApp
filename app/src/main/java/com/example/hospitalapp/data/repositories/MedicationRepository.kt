package com.example.hospitalapp.data.repositories

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.hospitalapp.data.local.dao.MedicationDao
import com.example.hospitalapp.data.local.dao.PatientDetailDao
import com.example.hospitalapp.data.local.dao.UserDao
import com.example.hospitalapp.data.local.entities.MedicationEntity
import com.example.hospitalapp.data.local.extensions.toMedicationResponse
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

class MedicationRepositoryImpl @Inject constructor(
    private val medicationDao: MedicationDao,
    private val userDao: UserDao,
    private val patientDetailDao: PatientDetailDao
) : MedicationRepository {

    private val currentDate = "2025-05-12 20:23:39" // Current UTC time
    private val currentUser = "amz202" // Current user's login

    override suspend fun getMedications(): List<MedicationResponse> {
        return medicationDao.getAllMedications().map {
            it.toMedicationResponse(userDao, patientDetailDao)
        }
    }

    override suspend fun getMedicationById(id: Long): MedicationResponse {
        return medicationDao.getMedicationById(id)?.toMedicationResponse(userDao, patientDetailDao)
            ?: throw IllegalStateException("Medication not found")
    }

    override suspend fun getMedicationsByAppointment(appointmentId: Long): List<MedicationResponse> {
        return medicationDao.getMedicationsByAppointment(appointmentId).map {
            it.toMedicationResponse(userDao, patientDetailDao)
        }
    }

    override suspend fun getPatientMedications(patientId: Long): List<MedicationResponse> {
        return medicationDao.getPatientMedications(patientId).map {
            it.toMedicationResponse(userDao, patientDetailDao)
        }
    }

    override suspend fun getActiveMedications(patientId: Long): List<MedicationResponse> {
        return medicationDao.getActiveMedications(patientId).map {
            it.toMedicationResponse(userDao, patientDetailDao)
        }
    }

    override suspend fun createMedication(request: MedicationRequest): MedicationResponse {
        val medicationEntity = MedicationEntity(
            patientId = request.patientId,
            appointmentId = request.appointmentId,
            name = request.name,
            dosage = request.dosage,
            frequency = request.frequency,
            startDate = request.startDate,
            endDate = request.endDate,
            instructions = request.instructions,
            active = true,
            createdAt = currentDate,
            updatedAt = currentDate
        )

        val id = medicationDao.insertMedication(medicationEntity)
        return getMedicationById(id)
    }

    override suspend fun updateMedication(id: Long, request: MedicationRequest): MedicationResponse {
        val existing = medicationDao.getMedicationById(id)
            ?: throw IllegalStateException("Medication not found")

        val updated = existing.copy(
            name = request.name,
            dosage = request.dosage,
            frequency = request.frequency,
            startDate = request.startDate,
            endDate = request.endDate,
            instructions = request.instructions,
            updatedAt = currentDate
        )

        medicationDao.updateMedication(updated)
        return getMedicationById(id)
    }

    override suspend fun deleteMedication(id: Long) {
        val medication = medicationDao.getMedicationById(id)
            ?: throw IllegalStateException("Medication not found")
        medicationDao.deleteMedication(medication)
    }
}