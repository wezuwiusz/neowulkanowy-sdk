package io.github.wulkanowy.sdk.scrapper.menu

import io.github.wulkanowy.sdk.scrapper.adapter.CustomDateAdapter
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class Menu(

    @SerialName("Data")
    @Serializable(with = CustomDateAdapter::class)
    val date: LocalDateTime,

    @SerialName("Dieta")
    val diet: String,

    @SerialName("Id")
    val id: Int,

    @SerialName("Posilki")
    val meals: List<Meal> = emptyList(),
)
