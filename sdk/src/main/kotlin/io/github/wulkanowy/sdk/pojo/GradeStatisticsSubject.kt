package io.github.wulkanowy.sdk.pojo

data class GradeStatisticsSubject(
    val subject: String,
    val average: String,
    val items: List<GradeStatisticsItem>
)
