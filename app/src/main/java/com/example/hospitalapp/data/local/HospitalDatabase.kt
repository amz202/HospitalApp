package com.example.hospitalapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.hospitalapp.data.local.dao.*
import com.example.hospitalapp.data.local.entities.*


@Database(
    entities = [
        UserEntity::class,
        PatientDetailEntity::class,
        DoctorDetailEntity::class,
        AppointmentEntity::class,
        MedicationEntity::class,
        VitalsEntity::class,
        ReportEntity::class,
        FeedbackEntity::class
    ],
    version = 1
)
abstract class HospitalDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun patientDetailDao(): PatientDetailDao
    abstract fun doctorDetailDao(): DoctorDetailDao
    abstract fun appointmentDao(): AppointmentDao
    abstract fun medicationDao(): MedicationDao
    abstract fun vitalsDao(): VitalsDao
    abstract fun reportDao(): ReportDao
    abstract fun feedbackDao(): FeedbackDao

    companion object {
        private var Instance: HospitalDatabase? = null

        fun getDatabase(context: Context): HospitalDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    HospitalDatabase::class.java,
                    "hospital_database"
                )
                    .build()
                    .also { Instance = it }
            }
        }
    }
}