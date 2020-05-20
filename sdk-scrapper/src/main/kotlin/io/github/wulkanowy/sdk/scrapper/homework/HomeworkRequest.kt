package io.github.wulkanowy.sdk.scrapper.homework

import com.google.gson.annotations.SerializedName
import java.util.Date

data class HomeworkRequest(

    @SerializedName("date")
    val date: Date,

    @SerializedName("schoolYear")
    val schoolYear: Int,

    @SerializedName("statusFilter")
    val statusFilter: Int
)
