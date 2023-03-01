package io.github.wulkanowy.sdk.mobile.exams

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Exam(

    @Json(name = "Id")
    val id: Int,

    @Json(name = "IdPrzedmiot")
    val subjectId: Int,

    @Json(name = "IdPracownik")
    val employeeId: Int,

    @Json(name = "IdOddzial")
    val classId: Int?,

    @Json(name = "IdPodzial")
    val divideId: Int?,

    @Json(name = "PodzialNazwa")
    val divideName: String?,

    @Json(name = "Rodzaj")
    val type: Boolean, // false - quiz, true - test

    @Json(name = "RodzajNumer")
    val typeNumber: Int,

    @Json(name = "Opis")
    val description: String,

    @Json(name = "Data")
    val date: Long,

    @Json(name = "DataTekst")
    val dateText: String,
)
