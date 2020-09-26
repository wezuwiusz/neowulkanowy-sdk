package io.github.wulkanowy.sdk.mobile.homework

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Homework(

    @Json(name = "Id")
    val id: Int,

    @Json(name = "IdUczen")
    val studentId: Int,

    @Json(name = "Data")
    val date: Long,

    @Json(name = "DataTekst")
    val dateText: String,

    @Json(name = "IdPracownik")
    val employeeId: Int,

    @Json(name = "IdPrzedmiot")
    val subjectId: Int,

    @Json(name = "Opis")
    val content: String
)
