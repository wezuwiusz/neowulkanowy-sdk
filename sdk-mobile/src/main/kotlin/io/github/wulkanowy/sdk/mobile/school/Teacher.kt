package io.github.wulkanowy.sdk.mobile.school

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Teacher(

    @Json(name = "IdPracownik")
    val employeeId: Int,

    @Json(name = "IdPrzedmiot")
    val subjectId: Int,

    @Json(name = "Rola")
    val role: String
)
