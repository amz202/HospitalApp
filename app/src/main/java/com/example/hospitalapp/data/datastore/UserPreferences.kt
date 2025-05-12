package com.example.hospitalapp.data.datastore


import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.hospitalapp.network.model.Gender
import com.example.hospitalapp.network.model.UserResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class UserPreferences(private val context: Context) {

    companion object {
        private val USER_ID_KEY = longPreferencesKey("user_id")
        private val USERNAME_KEY = stringPreferencesKey("username")
        private val EMAIL_KEY = stringPreferencesKey("email")
        private val FIRST_NAME_KEY = stringPreferencesKey("first_name")
        private val LAST_NAME_KEY = stringPreferencesKey("last_name")
        private val ROLE_KEY = stringPreferencesKey("role")
        private val GENDER_KEY = stringPreferencesKey("gender")
        private val DOB_KEY = stringPreferencesKey("dob")
        private val ADDRESS_KEY = stringPreferencesKey("address")
    }

    suspend fun saveUser(user: UserResponse) {
        context.dataStore.edit { preferences ->
            preferences[USER_ID_KEY] = user.id
        }
    }

    suspend fun saveUserId(userId: Long) {
        context.dataStore.edit { preferences ->
            preferences[USER_ID_KEY] = userId
        }
    }

    suspend fun getUserId(): Long? {
        return context.dataStore.data.map { preferences ->
            preferences[USER_ID_KEY]
        }.first()
    }

    suspend fun getUser(): UserInfo? {
        val preferences = context.dataStore.data.first()

        return try {
            UserInfo(
                id = preferences[USER_ID_KEY] ?: return null,
                username = preferences[USERNAME_KEY] ?: return null,
                email = preferences[EMAIL_KEY] ?: return null,
                fName = preferences[FIRST_NAME_KEY] ?: return null,
                lName = preferences[LAST_NAME_KEY] ?: return null,
                gender = Gender.valueOf(preferences[GENDER_KEY] ?: return null),
                dob = preferences[DOB_KEY] ?: return null,
                address = preferences[ADDRESS_KEY] ?: return null,
                role = preferences[ROLE_KEY] ?: return null
            )
        } catch (e: IllegalArgumentException) {
            // Handle case where Gender.valueOf fails
            null
        }
    }

    suspend fun clearUserData() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    data class UserInfo(
        val id: Long,
        val username: String,
        val email: String,
        val fName: String,
        val lName: String,
        val gender: Gender,
        val dob: String,
        val address: String,
        val role: String
    )
}