package com.openmobilehub.android.auth.sample.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "login_state")

class LoginState(val context: Context) {
    private val LOGIN_PROVIDER = stringPreferencesKey("login_provider")

    suspend fun loggedIn(provider: String) {
        context.dataStore.edit { preferences ->
            preferences[LOGIN_PROVIDER] = provider
        }
    }

    suspend fun loggedOut() {
        context.dataStore.edit { preferences ->
            preferences[LOGIN_PROVIDER] = ""
        }
    }

    fun getLoggedInProvider(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[LOGIN_PROVIDER]
        }
    }
}
