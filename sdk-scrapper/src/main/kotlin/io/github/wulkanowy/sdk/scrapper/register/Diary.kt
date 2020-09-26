package io.github.wulkanowy.sdk.scrapper.register

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.Date

@JsonClass(generateAdapter = true)
data class Diary(

    @Json(name = "Id")
    val id: Int,

    @Json(name = "IdUczen")
    val studentId: Int,

    @Json(name = "UczenImie")
    val studentName: String,

    @Json(name = "UczenImie2")
    val studentSecondName: String?,

    @Json(name = "UczenNazwisko")
    val studentSurname: String,

    @Json(name = "IsDziennik")
    val isDiary: Boolean,

    @Json(name = "IdDziennik")
    val diaryId: Int,

    @Json(name = "IdPrzedszkoleDziennik")
    val kindergartenDiaryId: Int,

    @Json(name = "Poziom")
    val level: Int,

    @Json(name = "Symbol")
    val symbol: String,

    @Json(name = "Nazwa")
    val name: String?,

    @Json(name = "DziennikRokSzkolny")
    val year: Int,

    @Json(name = "Okresy")
    val semesters: List<Semester>? = emptyList(),

    @Json(name = "UczenPelnaNazwa")
    val fullName: String
) {
    @JsonClass(generateAdapter = true)
    data class Semester(

        @Json(name = "NumerOkresu")
        val number: Int,

        @Json(name = "Poziom")
        val level: Int,

        @Json(name = "DataOd")
        val start: Date,

        @Json(name = "DataDo")
        val end: Date,

        @Json(name = "IdOddzial")
        val classId: Int,

        @Json(name = "IdJednostkaSprawozdawcza")
        val unitId: Int,

        @Json(name = "IsLastOkres")
        val isLast: Boolean,

        @Json(name = "Id")
        val id: Int
    )
}
