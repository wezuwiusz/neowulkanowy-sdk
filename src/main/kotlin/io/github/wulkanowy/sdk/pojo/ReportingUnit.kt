package io.github.wulkanowy.sdk.pojo

data class ReportingUnit(
    val id: Int,
    val short: String,
    val senderId: Int,
    val roles: List<Int>,
    val senderName: String
)
