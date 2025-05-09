package com.example.hospitalapp.network

import com.example.hospitalapp.network.model.*
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.http.*

interface ApiService {
    // Doctor endpoints
    @GET("api/doctors")
    suspend fun getAllDoctors(): List<DoctorResponse>

    @GET("api/doctors/{id}")
    suspend fun getDoctorById(@Path("id") id: Long): DoctorResponse

    @POST("api/doctors")
    suspend fun createDoctor(@Body doctor: DoctorRequest): DoctorResponse

    @PUT("api/doctors/{id}")
    suspend fun updateDoctor(
        @Path("id") id: Long,
        @Body doctor: DoctorRequest
    ): DoctorResponse

    @DELETE("api/doctors/{id}")
    suspend fun deleteDoctor(@Path("id") id: Long)

    @GET("api/doctors/{id}/patients")
    suspend fun getDoctorPatients(@Path("id") doctorId: Long): List<PatientResponse>

    @GET("api/doctors/specialization/{specialization}")
    suspend fun getDoctorsBySpecialization(
        @Path("specialization") specialization: String
    ): List<DoctorResponse>

    @GET("api/doctors/{id}/appointments")
    suspend fun getDoctorAppointments(@Path("id") doctorId: Long): List<AppointmentResponse>

    @PUT("api/doctors/{doctorId}/appointments/{appointmentId}/status")
    suspend fun updateAppointmentStatus(
        @Path("doctorId") doctorId: Long,
        @Path("appointmentId") appointmentId: Long,
        @Query("status") status: AppointmentStatus
    ): AppointmentResponse

    @POST("api/doctors/{doctorId}/appointments/{appointmentId}/medications")
    suspend fun prescribeMedication(
        @Path("doctorId") doctorId: Long,
        @Path("appointmentId") appointmentId: Long,
        @Body medication: MedicationRequest
    ): MedicationResponse

    @POST("api/doctors/{doctorId}/appointments/{appointmentId}/feedback")
    suspend fun provideFeedback(
        @Path("doctorId") doctorId: Long,
        @Path("appointmentId") appointmentId: Long,
        @Body feedback: FeedbackRequest
    ): FeedbackResponse

    // Patient endpoints
    @GET("api/patients")
    suspend fun getAllPatients(): List<PatientResponse>

    @GET("api/patients/{id}")
    suspend fun getPatientById(@Path("id") id: Long): PatientResponse

    @POST("api/patients")
    suspend fun createPatient(@Body patient: PatientRequest): PatientResponse

    @PUT("api/patients/{id}")
    suspend fun updatePatient(
        @Path("id") id: Long,
        @Body patient: PatientRequest
    ): PatientResponse

    @DELETE("api/patients/{id}")
    suspend fun deletePatient(@Path("id") id: Long)

    @GET("api/patients/{id}/vitals")
    suspend fun getPatientVitals(@Path("id") patientId: Long): List<VitalsResponse>

    @GET("api/patients/{id}/reports")
    suspend fun getPatientReports(@Path("id") patientId: Long): List<ReportResponse>

    @GET("api/patients/doctor/{doctorId}")
    suspend fun getPatientsByDoctor(@Path("doctorId") doctorId: Long): List<PatientResponse>

    // Admin endpoints
    @GET("api/admins")
    suspend fun getAllAdmins(): List<AdminResponse>

    @GET("api/admins/{id}")
    suspend fun getAdminById(@Path("id") id: Long): AdminResponse

    @POST("api/admins")
    suspend fun createAdmin(@Body admin: AdminRequest): AdminResponse

    @PUT("api/admins/{id}")
    suspend fun updateAdmin(
        @Path("id") id: Long,
        @Body admin: AdminRequest
    ): AdminResponse

    @DELETE("api/admins/{id}")
    suspend fun deleteAdmin(@Path("id") id: Long)

    // Admin management endpoints
    @GET("api/admins/users")
    suspend fun getAllUsers(): List<UserResponse>

    @GET("api/admins/users/{id}")
    suspend fun getUserById(@Path("id") id: Long): UserResponse

    @DELETE("api/admins/users/{id}")
    suspend fun deleteUser(@Path("id") id: Long)

    @GET("api/admins/stats/patients-count")
    suspend fun getTotalPatientsCount(): Long

    @GET("api/admins/stats/doctors-count")
    suspend fun getTotalDoctorsCount(): Long

    @GET("api/admins/stats/appointments-count")
    suspend fun getTotalAppointmentsCount(): Long

    @GET("api/admins/stats/pending-appointments-count")
    suspend fun getPendingAppointmentsCount(): Long

    // Appointment endpoints
    @GET("api/appointments")
    suspend fun getAllAppointments(): List<AppointmentResponse>

    @GET("api/appointments/{id}")
    suspend fun getAppointmentById(@Path("id") id: Long): AppointmentResponse

    @POST("api/appointments")
    suspend fun createAppointment(@Body appointment: AppointmentRequest): AppointmentResponse

    @PUT("api/appointments/{id}/status")
    suspend fun updateAppointmentStatus(
        @Path("id") id: Long,
        @Body status: AppointmentStatus
    ): AppointmentResponse

    @DELETE("api/appointments/{id}")
    suspend fun deleteAppointment(@Path("id") id: Long)

    // Report endpoints
    @GET("api/reports")
    suspend fun getAllReports(): List<ReportResponse>

    @GET("api/reports/{id}")
    suspend fun getReportById(@Path("id") id: Long): ReportResponse

    @POST("api/reports")
    suspend fun createReport(@Body report: ReportRequest): ReportResponse

    @GET("api/reports/patient/{patientId}")
    suspend fun getReportsByPatient(@Path("patientId") patientId: Long): List<ReportResponse>

    // Medication endpoints
    @GET("api/medications")
    suspend fun getAllMedications(): List<MedicationResponse>

    @GET("api/medications/{id}")
    suspend fun getMedicationById(@Path("id") id: Long): MedicationResponse

    @POST("api/medications")
    suspend fun createMedication(@Body medication: MedicationRequest): MedicationResponse

    @PUT("api/medications/{id}")
    suspend fun updateMedication(
        @Path("id") id: Long,
        @Body medication: MedicationRequest
    ): MedicationResponse

    @GET("api/medications/appointment/{appointmentId}")
    suspend fun getMedicationsByAppointment(@Path("appointmentId") appointmentId: Long): List<MedicationResponse>

    // Vitals endpoints
    @GET("api/vitals/{id}")
    suspend fun getVitalsById(@Path("id") id: Long): VitalsResponse

    @POST("api/vitals")
    suspend fun createVitals(@Body vitals: VitalsRequest): VitalsResponse

    @GET("api/patients/{id}/vitals/latest")
    suspend fun getLatestVitals(@Path("id") patientId: Long): VitalsResponse

    @GET("api/patients/{id}/vitals/critical")
    suspend fun getCriticalVitals(@Path("id") patientId: Long): List<VitalsResponse>

    @Multipart
    @POST("api/patients/{id}/vitals/upload")
    suspend fun uploadVitalsCSV(
        @Path("id") patientId: Long,
        @Part file: MultipartBody.Part
    ): ResponseBody

    // Feedback endpoints
    @GET("api/feedback/{id}")
    suspend fun getFeedbackById(@Path("id") id: Long): FeedbackResponse

    @POST("api/feedback")
    suspend fun createFeedback(@Body feedback: FeedbackRequest): FeedbackResponse

    @PUT("api/feedback/{id}")
    suspend fun updateFeedback(
        @Path("id") id: Long,
        @Body feedback: FeedbackRequest
    ): FeedbackResponse

    @GET("api/doctors/{id}/feedback")
    suspend fun getDoctorFeedback(@Path("id") id: Long): List<FeedbackResponse>

    @GET("api/patients/{id}/feedback")
    suspend fun getPatientFeedback(@Path("id") id: Long): List<FeedbackResponse>

    @GET("api/appointments/status")
    suspend fun getAppointmentsByStatus(@Query("status") status: AppointmentStatus): List<AppointmentResponse>

    @GET("api/appointments/patient/{patientId}/upcoming")
    suspend fun getUpcomingAppointmentsByPatient(@Path("patientId") patientId: Long): List<AppointmentResponse>

    @GET("api/appointments/doctor/{doctorId}/upcoming")
    suspend fun getUpcomingAppointmentsByDoctor(@Path("doctorId") doctorId: Long): List<AppointmentResponse>

    @GET("api/appointments/today")
    suspend fun getTodaysAppointments(): List<AppointmentResponse>

    @GET("api/medications/patient/{patientId}")
    suspend fun getMedicationsByPatient(@Path("patientId") patientId: Long): List<MedicationResponse>

    @POST("api/patients/{id}/vitals")
    suspend fun addVitals(
        @Path("id") patientId: Long,
        @Body vitals: VitalsRequest
    ): VitalsResponse
}