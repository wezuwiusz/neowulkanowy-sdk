package io.github.wulkanowy.sdk.scrapper.menu

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MealDetail(

    @SerialName("Label")
    val label: String,

    @SerialName("Wartosc")
    val value: String,
)
