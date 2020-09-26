package io.github.wulkanowy.sdk.scrapper.homework

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.Date

@JsonClass(generateAdapter = true)
data class HomeworkDay(

    @Json(name = "Date")
    val date: Date,

    @Json(name = "Homework")
    val items: List<Homework>,

    @Json(name = "Show")
    val show: Boolean
)
