package io.github.wulkanowy.sdk.pojo

import org.threeten.bp.LocalDateTime

data class Device(
    val id: Int,
    val name: String,
    val date: LocalDateTime
)
