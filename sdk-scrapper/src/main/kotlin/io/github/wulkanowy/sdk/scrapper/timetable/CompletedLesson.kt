package io.github.wulkanowy.sdk.scrapper.timetable

import io.github.wulkanowy.sdk.scrapper.adapter.CustomDateAdapter
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames
import java.time.LocalDateTime

@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class CompletedLesson(

    @SerialName("Data")
    @JsonNames("data")
    @Serializable(with = CustomDateAdapter::class)
    val date: LocalDateTime,

    @SerialName("NrLekcji")
    @JsonNames("nrLekcji")
    val number: Int = 0,

    @SerialName("Przedmiot")
    @JsonNames("przedmiot")
    val subject: String? = "",

    @SerialName("Temat")
    @JsonNames("tematOpis")
    val topic: String? = "",

    @SerialName("Nauczyciel")
    @JsonNames("nauczyciel")
    val teacher: String? = "",

    val teacherSymbol: String? = "",

    @SerialName("Zastepstwo")
    val substitution: String? = "",

    @SerialName("Nieobecnosc")
    val absence: String? = "",

    @SerialName("ZasobyPubliczne")
    val resources: String? = "",
)
