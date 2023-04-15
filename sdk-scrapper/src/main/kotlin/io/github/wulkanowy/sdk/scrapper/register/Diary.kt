package io.github.wulkanowy.sdk.scrapper.register

import io.github.wulkanowy.sdk.scrapper.adapter.CustomDateAdapter
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class Diary(

    @SerialName("Id")
    val id: Int,

    @SerialName("IdUczen")
    val studentId: Int,

    @SerialName("UczenImie")
    val studentName: String,

    @SerialName("UczenImie2")
    val studentSecondName: String?,

    @SerialName("UczenNazwisko")
    val studentSurname: String,

    @SerialName("UczenPseudonim")
    val studentNick: String?,

    @SerialName("IsDziennik")
    val isDiary: Boolean,

    @SerialName("IdDziennik")
    val diaryId: Int,

    @SerialName("IdPrzedszkoleDziennik")
    val kindergartenDiaryId: Int,

    @SerialName("IdWychowankowieDziennik")
    val fosterDiaryId: Int?,

    @SerialName("Poziom")
    val level: Int,

    @SerialName("Symbol")
    val symbol: String?,

    @SerialName("Nazwa")
    val name: String?,

    @SerialName("DziennikRokSzkolny")
    val year: Int,

    @SerialName("Okresy")
    val semesters: List<Semester>? = emptyList(),

    // @SerialName("UczenOddzialOkresy")
    // val classSemesters: List<Any>? = emptyList(),

    @Serializable(with = CustomDateAdapter::class)
    @SerialName("DziennikDataOd")
    val start: LocalDateTime,

    @Serializable(with = CustomDateAdapter::class)
    @SerialName("DziennikDataDo")
    val end: LocalDateTime,

    @SerialName("IdJednostkaSkladowa")
    val componentUnitId: Int?,

    @SerialName("IdSioTyp")
    val sioTypeId: Int?,

    @SerialName("IsDorosli")
    val isAdults: Boolean?,

    @SerialName("IsPolicealna")
    val isPostSecondary: Boolean?,

    @SerialName("Is13")
    val is13: Boolean?,

    @SerialName("IsArtystyczna")
    val isArtistic: Boolean?,

    @SerialName("IsArtystyczna13")
    val isArtistic13: Boolean?,

    @SerialName("IsSpecjalny")
    val isSpecial: Boolean?,

    @SerialName("IsPrzedszkola")
    val isKindergarten: Boolean?,

    @SerialName("IsWychowankowie")
    val isFoster: Boolean?,

    @SerialName("IsArchiwalny")
    val isArchived: Boolean?,

    @SerialName("IsOplaty")
    val isCharges: Boolean?,

    @SerialName("IsPlatnosci")
    val isPayments: Boolean?,

    @SerialName("IsPayButtonOn")
    val isPayButtonOn: Boolean?,

    @SerialName("CanMergeAccounts")
    val canMergeAccounts: Boolean?,

    @SerialName("UczenPelnaNazwa")
    val fullName: String,

    @SerialName("O365PassType")
    val o365PassType: Int?,

    @SerialName("IsAdult")
    val isAdult: Boolean?,

    @SerialName("IsAuthorized")
    val isAuthorized: Boolean?,

    @SerialName("Obywatelstwo")
    val citizenship: Int?,
) {
    @Serializable
    data class Semester(

        @SerialName("NumerOkresu")
        val number: Int,

        @SerialName("Poziom")
        val level: Int,

        @SerialName("DataOd")
        @Serializable(with = CustomDateAdapter::class)
        val start: LocalDateTime,

        @SerialName("DataDo")
        @Serializable(with = CustomDateAdapter::class)
        val end: LocalDateTime,

        @SerialName("IdOddzial")
        val classId: Int,

        @SerialName("IdJednostkaSprawozdawcza")
        val unitId: Int,

        @SerialName("IsLastOkres")
        val isLast: Boolean,

        @SerialName("Id")
        val id: Int,
    )
}
