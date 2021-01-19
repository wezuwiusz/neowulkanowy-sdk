package io.github.wulkanowy.sdk.scrapper

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ApiResponse<out T>(

    val success: Boolean,

    val data: T?,

    val feedback: Feedback? = null,

    val errorMessage: String? = null
) {

    @JsonClass(generateAdapter = true)
    data class Feedback(

        @Json(name = "Handled")
        val handled: Boolean?,

        @Json(name = "FType")
        val type: String,

        @Json(name = "Message")
        val message: String,

        @Json(name = "ExceptionMessage")
        val exceptionMessage: String?,

        @Json(name = "InnerExceptionMessage")
        val innerExceptionMessage: String?,

        @Json(name = "success")
        val success: Boolean? = null
    )
}
