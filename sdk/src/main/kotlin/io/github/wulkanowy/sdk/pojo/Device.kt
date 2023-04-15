package io.github.wulkanowy.sdk.pojo

import java.time.ZonedDateTime

data class Device(
    val id: Int,
    val deviceId: String,
    val name: String,
    val createDate: ZonedDateTime,
    val modificationDate: ZonedDateTime?,
)
