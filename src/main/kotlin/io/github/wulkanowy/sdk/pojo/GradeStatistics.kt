package io.github.wulkanowy.sdk.pojo

data class GradeStatistics(
    val semesterId: Int,
    val subject: String,
    val grade: String,
    val gradeValue: Int,
    val amount: Int
)
