package io.github.wulkanowy.sdk.mobile.grades

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GradeSummary(

    @Json(name = "IdPrzedmiot")
    val subjectId: Int = 0,

    @Json(name = "Wpis")
    val entry: String = "",

    @Json(name = "SredniaOcen")
    val average: String = "0",

    @Json(name = "SumaPunktow")
    val pointsSum: String = ""
)
