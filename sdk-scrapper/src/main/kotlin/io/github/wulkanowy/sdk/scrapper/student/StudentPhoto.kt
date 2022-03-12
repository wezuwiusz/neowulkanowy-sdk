package io.github.wulkanowy.sdk.scrapper.student

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StudentPhoto(

    @Json(name = "Status")
    val status: Long?,

    @Json(name = "Error")
    val error: Any?,

    @Json(name = "Warning")
    val warning: Any?,

    @Json(name = "ZdjecieBase64")
    val photoBase64: String?,

    @Json(name = "Id")
    val id: Long?,
)
