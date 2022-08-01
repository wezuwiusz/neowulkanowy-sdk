package io.github.wulkanowy.sdk.scrapper

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<out T>(
    val success: Boolean,
    val data: T?,
    val feedback: Feedback? = null,
    val errorMessage: String? = null
)

@Serializable
data class Feedback(

    @SerialName("Handled")
    val handled: Boolean?,

    @SerialName("FType")
    val type: String,

    @SerialName("Message")
    val message: String,

    @SerialName("ExceptionMessage")
    val exceptionMessage: String?,

    @SerialName("InnerExceptionMessage")
    val innerExceptionMessage: String?,

    @SerialName("success")
    val success: Boolean? = null,
)
