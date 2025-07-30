package com.example.minisofascore.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Player(
    val id: Int,
    val name: String,
    val slug: String,
    val sport: Sport,
    val team: Team3,
    val country: Country,
    val position: String,
    val dateOfBirth: String
)
