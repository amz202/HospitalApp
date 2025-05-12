package com.example.hospitalapp.data.repositories

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.hospitalapp.data.local.dao.PatientDetailDao
import com.example.hospitalapp.data.local.dao.VitalsDao
import com.example.hospitalapp.data.local.dao.UserDao
import com.example.hospitalapp.data.local.entities.UserEntity
import com.example.hospitalapp.data.local.entities.VitalsEntity
import com.example.hospitalapp.data.local.extensions.toVitalsResponse
import com.example.hospitalapp.network.model.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
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

    override suspend fun getVitalsById(id: Long): VitalsResponse {
        return vitalsDao.getVitalsById(id)?.toVitalsResponse(userDao, patientDetailDao)
            ?: throw IllegalStateException("Vitals not found")
    }

    override suspend fun getVitalsByPatient(patientId: Long): List<VitalsResponse> {
        return vitalsDao.getVitalsByPatient(patientId).first().map {
            it.toVitalsResponse(userDao, patientDetailDao)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
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
            recordedAt = LocalDateTime.now().toString(),
            critical = false,  // Default values for critical tracking
            criticalNotes = null,
            alertSent = false
        )

        val id = vitalsDao.insertVitals(vitalsEntity)
        return getVitalsById(id)
    }
}