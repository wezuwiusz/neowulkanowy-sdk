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

    @Json(name = "UczenPseudonim")
    val studentNick: String?,

    @Json(name = "IsDziennik")
    val isDiary: Boolean,

    @Json(name = "IdDziennik")
    val diaryId: Int,

    @Json(name = "IdPrzedszkoleDziennik")
    val kindergartenDiaryId: Int,

    @Json(name = "IdWychowankowieDziennik")
    val fosterDiaryId: Int?,

    @Json(name = "Poziom")
    val level: Int,

    @Json(name = "Symbol")
    val symbol: String?,

    @Json(name = "Nazwa")
    val name: String?,

    @Json(name = "DziennikRokSzkolny")
    val year: Int,

    @Json(name = "Okresy")
    val semesters: List<Semester>? = emptyList(),

    @Json(name = "UczenOddzialOkresy")
    val classSemesters: List<Any>? = emptyList(),

    @Json(name = "DziennikDataOd")
    val start: Date,

    @Json(name = "DziennikDataDo")
    val end: Date,

    @Json(name = "IdJednostkaSkladowa")
    val componentUnitId: Int?,

    @Json(name = "IdSioTyp")
    val sioTypeId: Int?,

    @Json(name = "IsDorosli")
    val isAdults: Boolean?,

    @Json(name = "IsPolicealna")
    val isPostSecondary: Boolean?,

    @Json(name = "Is13")
    val is13: Boolean?,

    @Json(name = "IsArtystyczna")
    val isArtistic: Boolean?,

    @Json(name = "IsArtystyczna13")
    val isArtistic13: Boolean?,

    @Json(name = "IsSpecjalny")
    val isSpecial: Boolean?,

    @Json(name = "IsPrzedszkola")
    val isKindergarten: Boolean?,

    @Json(name = "IsWychowankowie")
    val isFoster: Boolean?,

    @Json(name = "IsArchiwalny")
    val isArchived: Boolean?,

    @Json(name = "IsOplaty")
    val isCharges: Boolean?,

    @Json(name = "IsPlatnosci")
    val isPayments: Boolean?,

    @Json(name = "IsPayButtonOn")
    val isPayButtonOn: Boolean?,

    @Json(name = "CanMergeAccounts")
    val canMergeAccounts: Boolean?,

    @Json(name = "UczenPelnaNazwa")
    val fullName: String,

    @Json(name = "O365PassType")
    val o365PassType: Int?,

    @Json(name = "IsAdult")
    val isAdult: Boolean?,

    @Json(name = "IsAuthorized")
    val isAuthorized: Boolean?,

    @Json(name = "Obywatelstwo")
    val citizenship: Int?,
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
