package com.example.minisofascore.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Score(
    val total: Int? = null,
    val period1: Int? = null,
    val period2: Int? = null,
    val period3: Int? = null,
    val period4: Int? = null,
    val overtime: Int? = null
)