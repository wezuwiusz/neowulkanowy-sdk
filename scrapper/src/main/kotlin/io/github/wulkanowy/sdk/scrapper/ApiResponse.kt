package io.github.wulkanowy.sdk.scrapper

import com.google.gson.annotations.SerializedName

data class ApiResponse<out T>(

    val success: Boolean,

    val data: T?,

    val feedback: Feedback,

    val errorMessage: String = ""
) {

    data class Feedback(

        @SerializedName("Handled")
        val handled: Boolean,

        @SerializedName("FType")
        val type: String,

        @SerializedName("Message")
        val message: String,

        @SerializedName("ExceptionMessage")
        val exceptionMessage: String,

        @SerializedName("InnerExceptionMessage")
        val innerExceptionMessage: String,

        val success: Boolean
    )
}
