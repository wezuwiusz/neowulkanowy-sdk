package io.github.wulkanowy.sdk.scrapper.student

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class StudentPhoto(

    @SerialName("Status")
    @JsonNames("status")
    val status: Long? = null,

    // @SerialName("Error")
    // val error: Any?,

    // @SerialName("Warning")
    // val warning: Any?,

    @SerialName("ZdjecieBase64")
    @JsonNames("zdjecieBase64")
    val photoBase64: String?,

    @SerialName("Id")
    @JsonNames("id")
    val id: Long? = null,
)
