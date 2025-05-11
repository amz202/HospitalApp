package com.example.hospitalapp.data.datastore


import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
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
    }

    suspend fun saveUser(user: UserResponse) {
        context.dataStore.edit { preferences ->
            preferences[USER_ID_KEY] = user.id
            preferences[USERNAME_KEY] = user.username
            preferences[EMAIL_KEY] = user.email
            preferences[FIRST_NAME_KEY] = user.firstName
            preferences[LAST_NAME_KEY] = user.lastName
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

        return UserInfo(
            id = userId,
            username = username,
            email = email,
            firstName = firstName,
            lastName = lastName
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
        val firstName: String,
        val lastName: String
    )
}