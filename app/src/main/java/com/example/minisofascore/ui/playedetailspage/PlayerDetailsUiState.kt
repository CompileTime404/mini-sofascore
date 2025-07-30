package com.example.minisofascore.ui.playedetailspage

import com.example.minisofascore.data.model.Player
import com.example.minisofascore.data.model.Tournament

data class PlayerDetailsUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val player: Player? = null,
    val tournament: Tournament? = null
)