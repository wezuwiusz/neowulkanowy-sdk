package io.github.wulkanowy.sdk.mobile.school

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TeachersResponse(

    @Json(name = "NauczycieleSzkola")
    val schoolTeachers: List<Teacher>,

    @Json(name = "NauczycielePrzedmioty")
    val teachersSubjects: List<Teacher>,
)
