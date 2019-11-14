package io.github.wulkanowy.sdk.mobile.grades

import com.google.gson.annotations.SerializedName

data class GradesSummaryResponse(

    @SerializedName("OcenyPrzewidywane")
    val predicted: List<GradeSummary>,

    @SerializedName("OcenyKlasyfikacyjne")
    val evaluative: List<GradeSummary>,

    @SerializedName("SrednieOcen")
    val average: List<GradeSummary>
)
