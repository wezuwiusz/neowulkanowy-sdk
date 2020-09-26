package io.github.wulkanowy.sdk.scrapper.homework

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.Date

@JsonClass(generateAdapter = true)
data class HomeworkRequest(

    @Json(name = "date")
    val date: Date,

    @Json(name = "schoolYear")
    val schoolYear: Int,

    @Json(name = "statusFilter")
    val statusFilter: Int
)
