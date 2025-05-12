package com.example.hospitalapp.network

import com.example.hospitalapp.network.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    // Admin Endpoints
    @GET("api/admins")
    suspend fun getAdmins(): List<AdminResponse>

    @GET("api/admins/{id}")
    suspend fun getAdminById(@Path("id") id: Long): AdminResponse

    @POST("api/admins")
    suspend fun createAdmin(@Body admin: AdminRequest): AdminResponse

    @PUT("api/admins/{id}")
    suspend fun updateAdmin(@Path("id") id: Long, @Body admin: AdminRequest): AdminResponse

    @DELETE("api/admins/{id}")
    suspend fun deleteAdmin(@Path("id") id: Long)

    // User Management
    @GET("api/admins/users")
    suspend fun getAllUsers(): List<UserResponse>

    @DELETE("api/admins/users/{id}")
    suspend fun deleteUser(@Path("id") id: Long)

    @POST("api/users/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("api/users/signup")
    suspend fun register(@Body request: SignupRequest): Response<UserResponse>

    @GET("api/patients/{id}")
    suspend fun getPatient(@Path("id") id: Long): Response<PatientResponse>

    @PUT("api/patients/{id}")
    suspend fun updatePatient(
        @Path("id") id: Long,
        @Body request: PatientUpdateRequest
    ): Response<PatientResponse>

    @GET("api/users/{id}")
    suspend fun getUserById(@Path("id") id: Long): UserResponse

    @GET("api/users/by-email/{email}")
    suspend fun getUserByEmail(@Path("email") email: String): UserResponse

    @GET("api/users/by-username/{username}")
    suspend fun getUserByUsername(@Path("username") username: String): UserResponse

    // Patient Management
    @GET("api/patients")
    suspend fun getPatients(): List<PatientResponse>

    @GET("api/patients/{id}")
    suspend fun getPatientById(@Path("id") id: Long): PatientResponse

    @POST("api/patients")
    suspend fun createPatient(@Body patient: PatientRequest): PatientResponse

    @PUT("api/patients/{id}")
    suspend fun updatePatient(@Path("id") id: Long, @Body patient: PatientRequest): PatientResponse

    @DELETE("api/patients/{id}")
    suspend fun deletePatient(@Path("id") id: Long)

    @GET("api/patients/{id}/vitals")
    suspend fun getPatientVitals(@Path("id") patientId: Long): List<VitalsResponse>

    @GET("api/patients/{id}/reports")
    suspend fun getPatientReports(@Path("id") patientId: Long): List<ReportResponse>

    @GET("api/patients/doctor/{doctorId}")
    suspend fun getPatientsByDoctor(@Path("doctorId") doctorId: Long): List<PatientResponse>

    // Doctor Management
    @GET("api/doctors")
    suspend fun getDoctors(): List<DoctorResponse>

    @GET("api/doctors/{id}")
    suspend fun getDoctorById(@Path("id") id: Long): DoctorResponse

    @POST("api/doctors")
    suspend fun createDoctor(@Body doctor: DoctorRequest): DoctorResponse

    @PUT("api/doctors/{id}")
    suspend fun updateDoctor(@Path("id") id: Long, @Body doctor: DoctorRequest): DoctorResponse

    @DELETE("api/doctors/{id}")
    suspend fun deleteDoctor(@Path("id") id: Long)

    @GET("api/doctors/{id}/patients")
    suspend fun getDoctorPatients(@Path("id") doctorId: Long): List<PatientResponse>

    @GET("api/doctors/specialization/{specialization}")
    suspend fun getDoctorsBySpecialization(@Path("specialization") specialization: String): List<DoctorResponse>

    // Appointment Management
    @GET("api/appointments")
    suspend fun getAppointments(): List<AppointmentResponse>

    @GET("api/appointments/{id}")
    suspend fun getAppointmentById(@Path("id") id: Long): AppointmentResponse

    @POST("api/appointments")
    suspend fun createAppointment(@Body appointment: AppointmentRequest): AppointmentResponse

    @GET("api/appointments/patient/{patientId}")
    suspend fun getPatientAppointments(@Path("patientId") patientId: Long): List<AppointmentResponse>

    @GET("api/appointments/doctor/{doctorId}")
    suspend fun getDoctorAppointments(@Path("doctorId") doctorId: Long): List<AppointmentResponse>

    @GET("api/appointments/status")
    suspend fun getAppointmentsByStatus(@Query("status") status: AppointmentStatus): List<AppointmentResponse>

    @GET("api/appointments/patient/{patientId}/upcoming")
    suspend fun getUpcomingAppointmentsByPatient(@Path("patientId") patientId: Long): List<AppointmentResponse>

    @GET("api/appointments/doctor/{doctorId}/upcoming")
    suspend fun getUpcomingAppointmentsByDoctor(@Path("doctorId") doctorId: Long): List<AppointmentResponse>

    // Medication Management
    @GET("api/medications")
    suspend fun getMedications(): List<MedicationResponse>

    @GET("api/medications/{id}")
    suspend fun getMedicationById(@Path("id") id: Long): MedicationResponse

    @GET("api/medications/appointment/{appointmentId}")
    suspend fun getMedicationsByAppointment(@Path("appointmentId") appointmentId: Long): List<MedicationResponse>

    @GET("api/medications/patient/{patientId}")
    suspend fun getPatientMedications(@Path("patientId") patientId: Long): List<MedicationResponse>

    @GET("api/medications/patient/{patientId}/active")
    suspend fun getActiveMedications(@Path("patientId") patientId: Long): List<MedicationResponse>

    // Vitals Management
    @GET("api/vitals/{id}")
    suspend fun getVitalsById(@Path("id") id: Long): VitalsResponse

    @GET("api/vitals/patient/{patientId}")
    suspend fun getVitalsByPatient(@Path("patientId") patientId: Long): List<VitalsResponse>

    @POST("api/vitals")
    suspend fun createVitals(@Body vitals: VitalsRequest): VitalsResponse

    // Stats Endpoints
    @GET("api/admins/stats/patients-count")
    suspend fun getTotalPatientsCount(): Long

    @GET("api/admins/stats/doctors-count")
    suspend fun getTotalDoctorsCount(): Long

    @GET("api/admins/stats/appointments-count")
    suspend fun getTotalAppointmentsCount(): Long

    @GET("api/admins/stats/pending-appointments-count")
    suspend fun getPendingAppointmentsCount(): Long
    @GET("api/feedback/{id}")
    suspend fun getFeedbackById(@Path("id") id: Long): FeedbackResponse

    @GET("api/feedback/appointment/{appointmentId}")
    suspend fun getFeedbackByAppointment(@Path("appointmentId") appointmentId: Long): FeedbackResponse

    @GET("api/feedback/doctor/{doctorId}")
    suspend fun getFeedbackByDoctor(@Path("doctorId") doctorId: Long): List<FeedbackResponse>

    @GET("api/feedback/patient/{patientId}")
    suspend fun getFeedbackByPatient(@Path("patientId") patientId: Long): List<FeedbackResponse>

    @POST("api/feedback")
    suspend fun createFeedback(@Body feedback: FeedbackRequest): ResponseBody

    @PUT("api/feedback/{id}")
    suspend fun updateFeedback(@Path("id") id: Long, @Body feedback: FeedbackRequest): ResponseBody

    @GET("api/feedback/exists/{appointmentId}")
    suspend fun hasFeedback(@Path("appointmentId") appointmentId: Long): Boolean

    @GET("api/feedback/doctor/{doctorId}/pending")
    suspend fun getAppointmentsWithoutFeedback(@Path("doctorId") doctorId: Long): List<AppointmentResponse>

    // Report Endpoints
    @GET("api/reports")
    suspend fun getAllReports(): List<ReportResponse>

    @GET("api/reports/{id}")
    suspend fun getReportById(@Path("id") id: Long): ReportResponse

    @GET("api/reports/patient/{patientId}")
    suspend fun getReportsByPatient(@Path("patientId") patientId: Long): List<ReportResponse>

    @POST("api/reports/appointment/{appointmentId}")
    suspend fun generateReport(@Path("appointmentId") appointmentId: Long): ReportResponse

    @PUT("api/reports/{id}")
    suspend fun updateReport(@Path("id") id: Long, @Body report: ReportRequest): ReportResponse

    @DELETE("api/reports/{id}")
    suspend fun deleteReport(@Path("id") id: Long)

    @GET("api/reports/patient/{patientId}/between")
    suspend fun getReportsByPatientBetweenDates(
        @Path("patientId") patientId: Long,
        @Query("start") startDate: String,
        @Query("end") endDate: String
    ): List<ReportResponse>

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

    @PUT("api/appointments/{id}/status")
    suspend fun updateAppointmentStatus(
        @Path("id") id: Long,
        @Query("status") status: AppointmentStatus
    ): AppointmentResponse


}