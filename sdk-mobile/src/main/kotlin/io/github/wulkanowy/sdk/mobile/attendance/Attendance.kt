package io.github.wulkanowy.sdk.mobile.attendance

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Attendance(

    @Json(name = "IdKategoria")
    val categoryId: Int,

    @Json(name = "Numer")
    val number: Int,

    @Json(name = "IdPoraLekcji")
    val lessonTimeId: Int,

    @Json(name = "Dzien")
    val date: Long,

    @Json(name = "DzienTekst")
    val dateText: String,

    @Json(name = "IdPrzedmiot")
    val subjectId: Int,

    @Json(name = "PrzedmiotNazwa")
    val subjectName: String,
)
