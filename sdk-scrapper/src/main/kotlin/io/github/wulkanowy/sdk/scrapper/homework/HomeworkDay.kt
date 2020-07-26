package io.github.wulkanowy.sdk.scrapper.homework

import com.google.gson.annotations.SerializedName
import java.util.Date

data class HomeworkDay(

    @SerializedName("Date")
    val date: Date,

    @SerializedName("Homework")
    val items: List<Homework>,

    @SerializedName("Show")
    val show: Boolean
)
