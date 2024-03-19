package io.github.wulkanowy.sdk.scrapper.exams

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ExamDetailsPlus(

    @SerialName("nauczycielImieNazwisko")
    val teacher: String,

    @SerialName("opis")
    val description: String,

    // TODO: "linki" field
)
