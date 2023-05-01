package io.github.wulkanowy.sdk.pojo

data class Meal(
    val name: String,
    val ingredients: List<MealIngredient>,
    val details: List<MealDetail>,
    val allergens: List<String>,
)

data class MealIngredient(
    val recipe: String,
    val measurementUnit: String,
    val measurementUnitValue: Int,
)

data class MealDetail(
    val label: String,
    val value: String,
)
