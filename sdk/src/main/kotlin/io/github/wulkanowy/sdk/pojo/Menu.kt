package io.github.wulkanowy.sdk.pojo

import java.time.LocalDate

data class Menu(
    val date: LocalDate,
    val diet: String,
    val meals: List<Meal>,
)
