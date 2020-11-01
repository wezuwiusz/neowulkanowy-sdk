package io.github.wulkanowy.sdk.scrapper.grades

data class GradeStatisticsSubject(
    val subject: String,
    val average: String,
    val items: List<GradeStatisticsPartial>
)
