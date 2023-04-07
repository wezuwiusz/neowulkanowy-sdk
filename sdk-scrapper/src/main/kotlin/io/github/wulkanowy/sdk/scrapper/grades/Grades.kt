package io.github.wulkanowy.sdk.scrapper.grades

data class Grades(
    val details: List<Grade>,
    val summary: List<GradeSummary>,
    val isAverage: Boolean,
    val isPoints: Boolean,
    val isForAdults: Boolean,
    val type: Int,
)
