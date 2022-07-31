package io.github.wulkanowy.sdk.scrapper.grades

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GradesStatisticsRequest(

    @SerialName( "idOkres")
    val semesterId: Int
)
