package com.samuelribeiro.polyhome.storage

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

private val Context.dataStore by preferencesDataStore(name = "auth")

class TokenStorage(private val context: Context) {

    companion object {
        private val tokenKey = stringPreferencesKey("security_token")
    }

    suspend fun write(token: String) {
        context.dataStore.edit { preferences ->
            preferences[tokenKey] = token
        }
    }

    suspend fun read(): String? {
        val preferences = context.dataStore.data.first()
        return preferences[tokenKey]
    }

    suspend fun clear() {
        context.dataStore.edit { preferences ->
            preferences.remove(tokenKey)
        }
    }
}