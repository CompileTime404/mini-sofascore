package com.example.minisofascore.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Tournament(
    val id: Int,
    val name: String,
    val slug: String,
    val sport: Sport,
    val country: Country
)