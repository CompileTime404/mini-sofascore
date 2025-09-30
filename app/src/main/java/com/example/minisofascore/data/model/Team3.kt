package com.example.minisofascore.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Team3(
    val id: Int,
    val name: String,
    val country: Country,
    val managerName: String? = null,
    val venue: String? = null,
)