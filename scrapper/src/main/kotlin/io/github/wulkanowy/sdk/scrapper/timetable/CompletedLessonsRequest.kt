package io.github.wulkanowy.sdk.scrapper.timetable

import com.google.gson.annotations.SerializedName

class CompletedLessonsRequest(

    @SerializedName("poczatek")
    val startDate: String,

    @SerializedName("koniec")
    val endDate: String,

    @SerializedName("idPrzedmiot")
    val subject: Int = -1
)
