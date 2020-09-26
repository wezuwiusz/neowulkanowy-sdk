package io.github.wulkanowy.sdk.scrapper.attendance

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AttendanceSummaryRequest(
    @Json(name = "idPrzedmiot")
    val id: Int?
)
