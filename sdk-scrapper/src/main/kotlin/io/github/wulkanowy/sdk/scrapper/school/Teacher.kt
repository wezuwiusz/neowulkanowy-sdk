package io.github.wulkanowy.sdk.scrapper.school

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Teacher(

    @Json(name = "Nauczyciel")
    val name: String,

    @Json(name = "Nazwa")
    val subject: String
) {

    @Transient
    var short: String = ""
}
