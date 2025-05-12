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
        VitalsEntity::class,
        AppointmentEntity::class,
        MedicationEntity::class,
        FeedbackEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class HospitalDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun vitalsDao(): VitalsDao
    abstract fun appointmentDao(): AppointmentDao
    abstract fun medicationDao(): MedicationDao
    abstract fun feedbackDao(): FeedbackDao

    companion object {
        @Volatile
        private var Instance: HospitalDatabase? = null

        fun getDatabase(context: Context): HospitalDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    HospitalDatabase::class.java,
                    "hospital_database"
                )
                    .createFromAsset("database/initial_data.db") // Optional: for seeding initial data
                    .build()
                    .also { Instance = it }
            }
        }
    }
}