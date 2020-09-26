package io.github.wulkanowy.sdk.mobile.grades

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GradesSummaryResponse(

    @Json(name = "OcenyPrzewidywane")
    val predicted: List<GradeSummary>,

    @Json(name = "OcenyKlasyfikacyjne")
    val evaluative: List<GradeSummary>,

    @Json(name = "SrednieOcen")
    val average: List<GradeSummary>
)
