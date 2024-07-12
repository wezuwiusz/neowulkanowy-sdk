package io.github.wulkanowy.sdk.hebe.models

import io.github.wulkanowy.sdk.hebe.CustomDateAdapter
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class LuckyNumber(
    @SerialName("Number")
    val number: Int,
    @SerialName("Day")
    @Serializable(with = CustomDateAdapter::class)
    val day: LocalDate,
)
