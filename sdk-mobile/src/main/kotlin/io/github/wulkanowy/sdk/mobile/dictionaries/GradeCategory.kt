package io.github.wulkanowy.sdk.mobile.dictionaries

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GradeCategory(

    @Json(name = "Id")
    val id: Int,

    @Json(name = "Kod")
    val code: String,

    @Json(name = "Nazwa")
    val name: String,
)
