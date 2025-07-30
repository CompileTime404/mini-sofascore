package com.example.minisofascore.ui.leaguespage

import com.example.minisofascore.data.model.Tournament
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class LeaguesPageUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val tournaments: ImmutableList<Tournament> = persistentListOf(),
    val selectedSportSlug: String = "football"
)