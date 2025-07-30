package com.example.minisofascore.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Sport(
    val id: Int,
    val name: String,
    val slug: String
)