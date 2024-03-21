package io.github.wulkanowy.sdk.scrapper.timetable

import io.github.wulkanowy.sdk.scrapper.adapter.CustomDateAdapter
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
internal data class TimetablePlusHeader(
    @SerialName("dataDo")
    @Serializable(with = CustomDateAdapter::class)
    val dataDo: LocalDateTime,

    @SerialName("dataOd")
    @Serializable(with = CustomDateAdapter::class)
    val dataOd: LocalDateTime,

    @SerialName("nazwa")
    val nazwa: String,
)
