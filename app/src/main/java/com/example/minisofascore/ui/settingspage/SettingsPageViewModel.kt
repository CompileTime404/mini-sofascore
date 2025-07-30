package com.example.minisofascore.ui.settingspage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.minisofascore.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsPageViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val isDarkTheme: StateFlow<Boolean> = settingsRepository.isDarkTheme.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    fun toggleDarkTheme(enabled: Boolean){
        viewModelScope.launch {
            settingsRepository.setDarkTheme(enabled)
        }
    }
}