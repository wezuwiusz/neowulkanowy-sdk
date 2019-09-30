package io.github.wulkanowy.sdk.pojo

data class Recipient(
    val id: String,
    val name: String,
    val loginId: Int,
    val reportingUnitId: Int?,
    val role: Int,
    val hash: String,
    val shortName: String
)
