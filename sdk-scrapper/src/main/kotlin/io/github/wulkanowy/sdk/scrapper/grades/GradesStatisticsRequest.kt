package io.github.wulkanowy.sdk.scrapper.grades

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GradesStatisticsRequest(

    @Json(name = "idOkres")
    val semesterId: Int
)
