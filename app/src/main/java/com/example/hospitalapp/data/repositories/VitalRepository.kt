package com.example.hospitalapp.data.repositories

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.hospitalapp.data.local.dao.VitalsDao
import com.example.hospitalapp.data.local.dao.UserDao
import com.example.hospitalapp.data.local.entities.UserEntity
import com.example.hospitalapp.data.local.entities.VitalsEntity
import com.example.hospitalapp.network.model.*
import kotlinx.coroutines.flow.first
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

interface VitalsRepository {
    suspend fun getVitalsById(id: Long): VitalsResponse
    suspend fun getVitalsByPatient(patientId: Long): List<VitalsResponse>
    suspend fun createVitals(vitals: VitalsRequest): VitalsResponse
}

class VitalsRepositoryImpl (
    private val vitalsDao: VitalsDao,
    private val userDao: UserDao
) : VitalsRepository {

    @RequiresApi(Build.VERSION_CODES.O)
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    override suspend fun getVitalsById(id: Long): VitalsResponse {
        return vitalsDao.getVitalsById(id)?.toVitalsResponse()
            ?: throw IllegalStateException("Vitals not found")
    }

    override suspend fun getVitalsByPatient(patientId: Long): List<VitalsResponse> {
        return vitalsDao.getVitalsByPatient(patientId).first().map { it.toVitalsResponse() }
    }

    override suspend fun createVitals(vitals: VitalsRequest): VitalsResponse {
        val vitalsEntity = VitalsEntity(
            patientId = vitals.patientId,
            heartRate = vitals.heartRate,
            systolicPressure = vitals.systolicPressure,
            diastolicPressure = vitals.diastolicPressure,
            temperature = vitals.temperature,
            oxygenSaturation = vitals.oxygenSaturation,
            respiratoryRate = vitals.respiratoryRate,
            bloodSugar = vitals.bloodSugar,
            recordedAt = LocalDateTime.now().format(dateFormatter)
        )

        val id = vitalsDao.insertVitals(vitalsEntity)
        return getVitalsById(id)
    }

    private suspend fun VitalsEntity.toVitalsResponse(): VitalsResponse {
        val patient = userDao.getUserById(patientId)
            ?: throw IllegalStateException("Patient not found")

        return VitalsResponse(
            id = id,
            patient = patient.toPatientResponse(),
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

    private fun UserEntity.toPatientResponse(): PatientResponse {
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
            // Add any patient-specific fields here
            bloodGroup = null, // Add these fields to UserEntity if needed
            allergies = emptyList(),
            medicalHistory = emptyList()
        )
    }
}