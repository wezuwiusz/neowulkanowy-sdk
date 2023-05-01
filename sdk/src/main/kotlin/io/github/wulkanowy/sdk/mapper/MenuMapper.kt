package io.github.wulkanowy.sdk.mapper

import io.github.wulkanowy.sdk.pojo.Meal
import io.github.wulkanowy.sdk.pojo.MealDetail
import io.github.wulkanowy.sdk.pojo.MealIngredient
import io.github.wulkanowy.sdk.pojo.Menu
import io.github.wulkanowy.sdk.scrapper.menu.MealDetail as ScrapperMealDetail
import io.github.wulkanowy.sdk.scrapper.menu.MealIngredient as ScrapperMealIngredient
import io.github.wulkanowy.sdk.scrapper.menu.Menu as ScrapperMenu

internal fun List<ScrapperMenu>.mapMenu() = map {
    Menu(
        date = it.date.toLocalDate(),
        diet = it.diet,
        meals = it.meals.map { meal ->
            Meal(
                name = meal.name,
                ingredients = meal.ingredients.mapMealIngredients(),
                details = meal.details.mapMealDetails(),
                allergens = meal.allergens,
            )
        },
    )
}

private fun List<ScrapperMealIngredient>.mapMealIngredients() = map {
    MealIngredient(
        recipe = it.recipe,
        measurementUnit = it.measurementUnit,
        measurementUnitValue = it.measurementUnitValue,
    )
}

private fun List<ScrapperMealDetail>.mapMealDetails() = map {
    MealDetail(
        label = it.label,
        value = it.value,
    )
}
