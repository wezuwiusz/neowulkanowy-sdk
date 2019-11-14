package io.github.wulkanowy.sdk.scrapper.timetable

import com.google.gson.annotations.SerializedName

data class TimetableRequest(

    @SerializedName("data")
    val date: String
)
