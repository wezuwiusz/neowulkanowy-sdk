package io.github.wulkanowy.sdk.pojo

import java.time.LocalDateTime
import java.time.ZonedDateTime

data class Device(
    val id: Int,
    val deviceId: String,
    val name: String,
    @Deprecated("use createDateZoned instead")
    val createDate: LocalDateTime,
    @Deprecated("use modificationDateZoned instead")
    val modificationDate: LocalDateTime?,
    val createDateZoned: ZonedDateTime,
    val modificationDateZoned: ZonedDateTime?,
)
