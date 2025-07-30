package com.example.minisofascore.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import com.example.minisofascore.data.preferences.PreferenceKeys
import com.example.minisofascore.data.preferences.dataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    val isDarkTheme: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.DARK_THEME] == true
    }

    suspend fun setDarkTheme(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.DARK_THEME] = enabled
        }
    }
}