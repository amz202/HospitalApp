package com.example.hospitalapp.data.repositories

import com.example.hospitalapp.data.local.dao.*
import com.example.hospitalapp.data.local.entities.ReportEntity
import com.example.hospitalapp.data.local.entities.VitalsEntity
import com.example.hospitalapp.data.local.extensions.toReportResponse
import com.example.hospitalapp.data.local.extensions.toVitalsResponse
import com.example.hospitalapp.network.model.*
import kotlinx.coroutines.flow.first
import javax.inject.Inject

interface VitalsRepository {
    suspend fun getVitalsById(id: Long): VitalsResponse
    suspend fun getVitalsByPatient(patientId: Long): List<VitalsResponse>
    suspend fun createVitals(vitals: VitalsRequest): VitalsResponse
}

class VitalsRepositoryImpl @Inject constructor(
    private val vitalsDao: VitalsDao,
    private val userDao: UserDao,
    private val patientDetailDao: PatientDetailDao
) : VitalsRepository {

    private val currentDate = "2025-05-12 20:32:47" // Current UTC time
    private val currentUser = "amz202" // Current user's login

    override suspend fun getVitalsById(id: Long): VitalsResponse {
        return vitalsDao.getVitalsById(id)?.toVitalsResponse(userDao, patientDetailDao)
            ?: throw IllegalStateException("Vitals not found")
    }

    override suspend fun getVitalsByPatient(patientId: Long): List<VitalsResponse> {
        return vitalsDao.getVitalsByPatient(patientId).first().map {
            it.toVitalsResponse(userDao, patientDetailDao)
        }
    }

    override suspend fun createVitals(request: VitalsRequest): VitalsResponse {
        val vitalsEntity = VitalsEntity(
            patientId = request.patientId,
            heartRate = request.heartRate,
            systolicPressure = request.systolicPressure,
            diastolicPressure = request.diastolicPressure,
            temperature = request.temperature?.toDouble(),
            oxygenSaturation = request.oxygenSaturation,
            respiratoryRate = request.respiratoryRate,
            bloodSugar = request.bloodSugar,
            recordedAt = currentDate,
            critical = false,  // Default values for critical tracking
            criticalNotes = null,
            alertSent = false
        )

        val id = vitalsDao.insertVitals(vitalsEntity)
        return getVitalsById(id)
    }
}