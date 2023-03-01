package io.github.wulkanowy.sdk.scrapper.attendance

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AttendanceResponse(

    @SerialName("UsprawiedliwieniaAktywne")
    val excuseActive: Boolean,

    @SerialName("Frekwencje")
    val lessons: List<Attendance>,

    @SerialName("UsprawiedliwieniaWyslane")
    val sentExcuses: List<SentExcuse>,
)
