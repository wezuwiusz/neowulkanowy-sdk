package io.github.wulkanowy.sdk.pojo

data class GradePointsStatistics(
    val semesterId: Int,
    val subject: String,
    val others: Double,
    val student: Double
)
