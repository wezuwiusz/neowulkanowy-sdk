package io.github.wulkanowy.sdk.scrapper.attendance

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AttendanceExcusePlusRequest(
    @SerialName("key")
    val key: String,
    @SerialName("tresc")
    val content: String,
    @SerialName("usprawiedliwienia")
    val excuses: List<AttendanceExcusePlusRequestItem>,
)

@Serializable
internal data class AttendanceExcusePlusRequestItem(
    @SerialName("data")
    val date: String,
    @SerialName("idPoraLekcji")
    val lessonHourId: Int?,
)
