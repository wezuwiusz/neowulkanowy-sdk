package io.github.wulkanowy.sdk.pojo

import org.threeten.bp.LocalDateTime

data class Device(
    val id: Int,
    val deviceId: String,
    val name: String,
    val createDate: LocalDateTime,
    val modificationDate: LocalDateTime?
)
