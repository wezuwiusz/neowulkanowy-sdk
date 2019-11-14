package io.github.wulkanowy.sdk.mobile.grades

import com.google.gson.annotations.SerializedName

data class GradeSummary(

    @SerializedName("IdPrzedmiot")
    val subjectId: Int = 0,

    @SerializedName("Wpis")
    val entry: String = "",

    @SerializedName("SredniaOcen")
    val average: String = "",

    @SerializedName("SumaPunktow")
    val pointsSum: String = ""
)
