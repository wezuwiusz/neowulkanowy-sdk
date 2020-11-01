package io.github.wulkanowy.sdk.scrapper.grades

data class GradeStatisticsAnnualItem(
    val subject: String,
    val grade: Int,
    val amount: Int,
    val isStudentHere: Boolean
)
