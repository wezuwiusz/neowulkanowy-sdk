package io.github.wulkanowy.api.register

import com.google.gson.annotations.SerializedName
import java.util.Date

data class Diary(

    @SerializedName("Id")
    val id: Int,

    @SerializedName("IdUczen")
    val studentId: Int,

    @SerializedName("UczenImie")
    val studentName: String,

    @SerializedName("UczenImie2")
    val studentSecondName: String,

    @SerializedName("UczenNazwisko")
    val studentSurname: String,

    @SerializedName("IsDziennik")
    val isDiary: Boolean,

    @SerializedName("IdDziennik")
    val diaryId: Int,

    @SerializedName("IdPrzedszkoleDziennik")
    val kindergartenDiaryId: Int,

    @SerializedName("Poziom")
    val level: Int,

    @SerializedName("Symbol")
    val symbol: String,

    @SerializedName("Nazwa")
    val name: String?,

    @SerializedName("DziennikRokSzkolny")
    val year: Int,

    @SerializedName("Okresy")
    val semesters: List<Semester> = emptyList(),

    @SerializedName("UczenPelnaNazwa")
    val fullName: String
) {
    data class Semester(

        @SerializedName("NumerOkresu")
        val number: Int,

        @SerializedName("Poziom")
        val level: Int,

        @SerializedName("DataOd")
        val start: Date,

        @SerializedName("DataDo")
        val end: Date,

        @SerializedName("IdOddzial")
        val classId: Int,

        @SerializedName("IdJednostkaSprawozdawcza")
        val unitId: Int,

        @SerializedName("IsLastOkres")
        val isLast: Boolean,

        @SerializedName("Id")
        val id: Int
    )
}
