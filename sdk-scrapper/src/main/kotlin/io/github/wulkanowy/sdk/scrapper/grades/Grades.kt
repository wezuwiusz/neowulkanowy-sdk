package io.github.wulkanowy.sdk.scrapper.grades

data class Grades(
    val details: List<Grade>,
    val summary: List<GradeSummary>,
    val descriptive: List<GradeDescriptive>,
    val isAverage: Boolean,
    val isPoints: Boolean,
    val isForAdults: Boolean,
    val type: Int,
)
