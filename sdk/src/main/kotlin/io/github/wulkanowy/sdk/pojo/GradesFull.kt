package io.github.wulkanowy.sdk.pojo

data class GradesFull(
    val details: List<Grade>,
    val summary: List<GradeSummary>,
    val isAverage: Boolean,
    val isPoints: Boolean,
    val isForAdults: Boolean,
    val type: Int,
)
