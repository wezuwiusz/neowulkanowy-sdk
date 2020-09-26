package io.github.wulkanowy.sdk.scrapper.school

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class SchoolAndTeachersResponse {

    @Json(name = "Nauczyciele")
    var teachers: List<Teacher> = emptyList()

    @Json(name = "Szkola")
    lateinit var school: School
}
