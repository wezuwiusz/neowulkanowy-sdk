package io.github.wulkanowy.sdk.scrapper.menu

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MealIngredient(

    @SerialName("Licznik")
    val counter: Int,

    @SerialName("Receptura")
    val recipe: String,

    @SerialName("JednostkaMiary")
    val measurementUnit: String,

    @SerialName("WartoscJednostkiMiary")
    val measurementUnitValue: Int,
)
