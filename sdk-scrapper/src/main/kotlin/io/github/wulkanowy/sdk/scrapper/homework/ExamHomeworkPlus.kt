package io.github.wulkanowy.sdk.scrapper.homework

import io.github.wulkanowy.sdk.scrapper.adapter.CustomDateAdapter
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
internal data class ExamHomeworkPlus(

    @SerialName("typ")
    val type: Int,

    @SerialName("przedmiotNazwa")
    val subject: String,

    @SerialName("data")
    @Serializable(with = CustomDateAdapter::class)
    val date: LocalDateTime,

    @SerialName("id")
    val id: Int,
)
