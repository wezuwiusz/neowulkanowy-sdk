package io.github.wulkanowy.sdk.scrapper.grades

import io.github.wulkanowy.sdk.scrapper.adapter.CustomDateAdapter
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
internal data class GradeSemester(
    @Serializable(with = CustomDateAdapter::class)
    @SerialName("dataDo")
    val dataDo: LocalDateTime,
    @SerialName("dataOd")
    @Serializable(with = CustomDateAdapter::class)
    val dataOd: LocalDateTime,
    @SerialName("id")
    val id: Int,
    @SerialName("numerOkresu")
    val numerOkresu: Int,
)
