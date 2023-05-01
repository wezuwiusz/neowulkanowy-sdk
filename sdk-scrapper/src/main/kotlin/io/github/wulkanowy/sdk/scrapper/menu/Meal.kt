package io.github.wulkanowy.sdk.scrapper.menu

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Meal(

    @SerialName("Alergeny")
    val allergens: List<String>,

    @SerialName("Nazwa")
    val name: String,

    @SerialName("Sklad")
    val ingredients: List<MealIngredient>,

    @SerialName("Szczegoly")
    val details: List<MealDetail>,
)
