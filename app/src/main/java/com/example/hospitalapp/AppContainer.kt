package com.example.hospitalapp
import android.app.Application
import com.example.hospitalapp.data.AppContainer
import com.example.hospitalapp.data.DefaultAppContainer
import com.example.hospitalapp.data.datastore.UserPreferences

class HospitalApplication : Application() {
    lateinit var container: AppContainer
    lateinit var userPreferences: UserPreferences

    override fun onCreate() {
        super.onCreate()
        userPreferences = UserPreferences(this)
        container = DefaultAppContainer(this)
    }
}