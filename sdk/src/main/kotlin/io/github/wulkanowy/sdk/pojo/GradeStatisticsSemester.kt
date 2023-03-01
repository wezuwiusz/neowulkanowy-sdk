package io.github.wulkanowy.sdk.pojo

data class GradeStatisticsSemester(
    val subject: String,
    val items: List<GradeStatisticsSemesterSubItem>,
)
