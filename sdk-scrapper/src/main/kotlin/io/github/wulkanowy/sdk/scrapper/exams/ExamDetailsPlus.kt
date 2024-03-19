package io.github.wulkanowy.sdk.scrapper.exams

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class ExamDetailsPlus(

    @SerialName("nauczycielImieNazwisko")
    val teacher: String,

    @SerialName("opis")
    val description: String,

    // TODO: "linki" field
)
