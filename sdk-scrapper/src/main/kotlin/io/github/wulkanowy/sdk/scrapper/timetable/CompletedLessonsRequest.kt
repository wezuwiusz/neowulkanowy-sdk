package io.github.wulkanowy.sdk.scrapper.timetable

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class CompletedLessonsRequest(

    @SerialName("poczatek")
    val startDate: String,

    @SerialName("koniec")
    val endDate: String,

    @SerialName("idPrzedmiot")
    val subject: Int = -1
)
