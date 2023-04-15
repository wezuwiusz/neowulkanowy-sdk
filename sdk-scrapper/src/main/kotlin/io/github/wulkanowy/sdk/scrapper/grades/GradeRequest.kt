package io.github.wulkanowy.sdk.scrapper.grades

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GradeRequest(

    @SerialName("okres")
    val semesterId: Int?,
)
