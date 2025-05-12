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
        private val ROLES_KEY = stringSetPreferencesKey("roles")  // Add this
        private val GENDER_KEY = stringPreferencesKey("gender")   // Add this
        private val DOB_KEY = stringPreferencesKey("dob")        // Add this
        private val ADDRESS_KEY = stringPreferencesKey("address") // Add this
    }

    suspend fun saveUser(user: UserResponse) {
        context.dataStore.edit { preferences ->
            preferences[USER_ID_KEY] = user.id
            preferences[USERNAME_KEY] = user.username
            preferences[EMAIL_KEY] = user.email
            preferences[FIRST_NAME_KEY] = user.fName
            preferences[LAST_NAME_KEY] = user.lName
            preferences[ROLES_KEY] = user.roles
            preferences[GENDER_KEY] = user.gender.toString()
            preferences[DOB_KEY] = user.dob
            preferences[ADDRESS_KEY] = user.address
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

        val username = preferences[USERNAME_KEY] ?: return null
        val email = preferences[EMAIL_KEY] ?: return null
        val firstName = preferences[FIRST_NAME_KEY] ?: return null
        val lastName = preferences[LAST_NAME_KEY] ?: return null
        val userId = preferences[USER_ID_KEY] ?: return null
        val roles = preferences[ROLES_KEY] ?: return null
        val gender = preferences[GENDER_KEY]?.let { Gender.valueOf(it) } ?: return null
        val dob = preferences[DOB_KEY] ?: return null
        val address = preferences[ADDRESS_KEY] ?: return null

        return UserInfo(
            id = userId,
            username = username,
            email = email,
            fName = firstName,
            lName = lastName,
            gender = gender,
            dob = dob,
            address = address,
            roles = roles
        )
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
        val roles: Set<String>
    )
}