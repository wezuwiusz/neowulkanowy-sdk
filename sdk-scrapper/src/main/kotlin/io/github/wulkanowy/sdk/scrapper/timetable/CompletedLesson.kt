package io.github.wulkanowy.sdk.scrapper.timetable

import io.github.wulkanowy.sdk.scrapper.adapter.CustomDateAdapter
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class CompletedLesson(

    @SerialName("Data")
    @Serializable(with = CustomDateAdapter::class)
    val date: LocalDateTime,

    @SerialName("NrLekcji")
    val number: Int = 0,

    @SerialName("Przedmiot")
    val subject: String? = "",

    @SerialName("Temat")
    val topic: String? = "",

    @SerialName("Nauczyciel")
    val teacher: String? = "",

    val teacherSymbol: String? = "",

    @SerialName("Zastepstwo")
    val substitution: String? = "",

    @SerialName("Nieobecnosc")
    val absence: String? = "",

    @SerialName("ZasobyPubliczne")
    val resources: String? = "",
)
