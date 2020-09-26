package io.github.wulkanowy.sdk.scrapper.attendance

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Subject(

    @Json(name = "Nazwa")
    var name: String = "Wszystkie",

    @Json(name = "Id")
    var value: Int = -1
)
