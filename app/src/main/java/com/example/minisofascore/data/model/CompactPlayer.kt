package com.example.minisofascore.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CompactPlayer(
    val id: Int,
    @SerialName("name")
    val name: String,
)