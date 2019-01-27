package io.github.wulkanowy.api.timetable

import com.google.gson.annotations.SerializedName

data class TimetableRequest(

    @SerializedName("data")
    val date: String
)
