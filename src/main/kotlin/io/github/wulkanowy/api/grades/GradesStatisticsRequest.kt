package io.github.wulkanowy.api.grades

import com.google.gson.annotations.SerializedName

data class GradesStatisticsRequest(

    @SerializedName("idOkres")
    val semesterId: Int
)
