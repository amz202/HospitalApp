package com.example.hospitalapp.network

import com.example.hospitalapp.network.model.AdminRequest
import com.example.hospitalapp.network.model.AdminResponse
import com.example.hospitalapp.network.model.AppointmentRequest
import com.example.hospitalapp.network.model.AppointmentResponse
import com.example.hospitalapp.network.model.AppointmentStatus
import com.example.hospitalapp.network.model.DoctorRequest
import com.example.hospitalapp.network.model.DoctorResponse
import com.example.hospitalapp.network.model.FeedbackRequest
import com.example.hospitalapp.network.model.FeedbackResponse
import com.example.hospitalapp.network.model.MedicationRequest
import com.example.hospitalapp.network.model.MedicationResponse
import com.example.hospitalapp.network.model.PatientRequest
import com.example.hospitalapp.network.model.PatientResponse
import com.example.hospitalapp.network.model.ReportRequest
import com.example.hospitalapp.network.model.ReportResponse
import com.example.hospitalapp.network.model.VitalsRequest
import com.example.hospitalapp.network.model.VitalsResponse
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {
    // Patient endpoints
    @GET("patients/{id}")
    suspend fun getPatient(@Path("id") id: Long): PatientResponse

    @GET("patients")
    suspend fun getPatients(): List<PatientResponse>

    @POST("patients")
    suspend fun createPatient(@Body patient: PatientRequest): ResponseBody

    @PUT("patients/{id}")
    suspend fun updatePatient(
        @Path("id") id: Long,
        @Body patient: PatientRequest
    ): ResponseBody

    @DELETE("patients/{id}")
    suspend fun deletePatient(@Path("id") id: Long): ResponseBody

    // Doctor endpoints
    @GET("doctors/{id}")
    suspend fun getDoctor(@Path("id") id: Long): DoctorResponse

    @GET("doctors")
    suspend fun getDoctors(): List<DoctorResponse>

    @POST("doctors")
    suspend fun createDoctor(@Body doctor: DoctorRequest): ResponseBody

    @PUT("doctors/{id}")
    suspend fun updateDoctor(
        @Path("id") id: Long,
        @Body doctor: DoctorRequest
    ): ResponseBody

    @DELETE("doctors/{id}")
    suspend fun deleteDoctor(@Path("id") id: Long): ResponseBody

    @GET("doctors/{id}/appointments")
    suspend fun getDoctorAppointments(@Path("id") id: Long): List<AppointmentResponse>

    // Admin endpoints
    @GET("admins/{id}")
    suspend fun getAdmin(@Path("id") id: Long): AdminResponse

    @GET("admins")
    suspend fun getAdmins(): List<AdminResponse>

    @POST("admins")
    suspend fun createAdmin(@Body admin: AdminRequest): ResponseBody

    @PUT("admins/{id}")
    suspend fun updateAdmin(
        @Path("id") id: Long,
        @Body admin: AdminRequest
    ): ResponseBody

    @DELETE("admins/{id}")
    suspend fun deleteAdmin(@Path("id") id: Long): ResponseBody

    // Appointment endpoints
    @GET("appointments/{id}")
    suspend fun getAppointment(@Path("id") id: Long): AppointmentResponse

    @GET("appointments")
    suspend fun getAppointments(): List<AppointmentResponse>

    @POST("appointments")
    suspend fun createAppointment(@Body appointment: AppointmentRequest): ResponseBody

    @PUT("appointments/{id}/status")
    suspend fun updateAppointmentStatus(
        @Path("id") id: Long,
        @Body status: AppointmentStatus
    ): ResponseBody

    @GET("patients/{id}/appointments")
    suspend fun getPatientAppointments(@Path("id") id: Long): List<AppointmentResponse>

    // Vitals endpoints
    @GET("vitals/{id}")
    suspend fun getVitals(@Path("id") id: Long): VitalsResponse

    @GET("patients/{id}/vitals")
    suspend fun getPatientVitals(@Path("id") id: Long): List<VitalsResponse>

    @POST("vitals")
    suspend fun createVitals(@Body vitals: VitalsRequest): ResponseBody

    @GET("patients/{id}/vitals/latest")
    suspend fun getLatestVitals(@Path("id") id: Long): VitalsResponse

    @GET("patients/{id}/vitals/critical")
    suspend fun getCriticalVitals(@Path("id") id: Long): List<VitalsResponse>

    // Medication endpoints
    @GET("medications/{id}")
    suspend fun getMedication(@Path("id") id: Long): MedicationResponse

    @GET("medications")
    suspend fun getMedications(): List<MedicationResponse>

    @POST("medications")
    suspend fun createMedication(@Body medication: MedicationRequest): ResponseBody

    @PUT("medications/{id}")
    suspend fun updateMedication(
        @Path("id") id: Long,
        @Body medication: MedicationRequest
    ): ResponseBody

    @GET("patients/{id}/medications")
    suspend fun getPatientMedications(@Path("id") id: Long): List<MedicationResponse>

    @GET("patients/{id}/medications/active")
    suspend fun getActiveMedications(@Path("id") id: Long): List<MedicationResponse>

    @GET("doctors/{id}/medications")
    suspend fun getDoctorPrescribedMedications(@Path("id") id: Long): List<MedicationResponse>

    // Feedback endpoints
    @GET("feedback/{id}")
    suspend fun getFeedback(@Path("id") id: Long): FeedbackResponse

    @POST("feedback")
    suspend fun createFeedback(@Body feedback: FeedbackRequest): ResponseBody

    @PUT("feedback/{id}")
    suspend fun updateFeedback(
        @Path("id") id: Long,
        @Body feedback: FeedbackRequest
    ): ResponseBody

    @GET("doctors/{id}/feedback")
    suspend fun getDoctorFeedback(@Path("id") id: Long): List<FeedbackResponse>

    @GET("patients/{id}/feedback")
    suspend fun getPatientFeedback(@Path("id") id: Long): List<FeedbackResponse>

    // Report endpoints
    @GET("reports/{id}")
    suspend fun getReport(@Path("id") id: Long): ReportResponse

    @POST("reports")
    suspend fun createReport(@Body report: ReportRequest): ResponseBody

    @GET("patients/{id}/reports")
    suspend fun getPatientReports(@Path("id") id: Long): List<ReportResponse>

    @GET("doctors/{id}/reports")
    suspend fun getDoctorReports(@Path("id") id: Long): List<ReportResponse>

    // File upload for reports
    @Multipart
    @POST("reports/{id}/upload")
    suspend fun uploadReportFile(
        @Path("id") id: Long,
        @Part file: MultipartBody.Part
    ): ResponseBody

    // Vitals CSV upload
    @Multipart
    @POST("patients/{id}/vitals/upload")
    suspend fun uploadVitalsCSV(
        @Path("id") id: Long,
        @Part file: MultipartBody.Part
    ): ResponseBody
}