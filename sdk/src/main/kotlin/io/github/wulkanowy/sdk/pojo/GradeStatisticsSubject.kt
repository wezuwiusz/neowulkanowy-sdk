package io.github.wulkanowy.sdk.pojo

data class GradeStatisticsSubject(
    val subject: String,
    val classAverage: String,
    val studentAverage: String,
    val classItems: List<GradeStatisticsItem>,
    val studentItems: List<GradeStatisticsItem>
)
