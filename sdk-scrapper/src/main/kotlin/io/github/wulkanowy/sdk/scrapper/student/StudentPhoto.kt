package io.github.wulkanowy.sdk.scrapper.student

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StudentPhoto(

    @SerialName("Status")
    val status: Long?,

    // @SerialName("Error")
    // val error: Any?,

    // @SerialName("Warning")
    // val warning: Any?,

    @SerialName("ZdjecieBase64")
    val photoBase64: String?,

    @SerialName("Id")
    val id: Long?,
)
