package io.github.wulkanowy.sdk.scrapper.grades

import com.google.gson.annotations.SerializedName

data class GradesStatisticsRequest(

    @SerializedName("idOkres")
    val semesterId: Int
)
