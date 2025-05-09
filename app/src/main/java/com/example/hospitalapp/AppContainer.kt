package com.example.hospitalapp
import android.app.Application
import com.example.hospitalapp.data.AppContainer
import com.example.hospitalapp.data.DefaultAppContainer

class HospitalApplication: Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer()
    }
}