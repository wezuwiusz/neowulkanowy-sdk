package io.github.wulkanowy.sdk.mobile.grades

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Grade(

    @Json(name = "Id")
    val id: Int,

    @Json(name = "Pozycja")
    val position: Int,

    @Json(name = "PrzedmiotPozycja")
    val subjectPosition: Int,

    @Json(name = "IdPrzedmiot")
    val subjectId: Int,

    @Json(name = "IdKategoria")
    val categoryId: Int,

    @Json(name = "Wpis")
    val entry: String,

    @Json(name = "Wartosc")
    val value: Double,

    @Json(name = "WagaModyfikatora")
    val modificationWeight: Double?,

    @Json(name = "WagaOceny")
    val gradeWeight: Double,

    @Json(name = "Licznik")
    val counter: Double?,

    @Json(name = "Mianownik")
    val denominator: Int?,

    @Json(name = "Komentarz")
    val comment: String?,

    @Json(name = "Waga")
    val weight: String,

    @Json(name = "Opis")
    val description: String,

    @Json(name = "DataUtworzenia")
    val creationDate: Long,

    @Json(name = "DataUtworzeniaTekst")
    val creationDateText: String,

    @Json(name = "DataModyfikacji")
    val modificationDate: Long,

    @Json(name = "DataModyfikacjiTekst")
    val modificationDateText: String,

    @Json(name = "IdPracownikD")
    val employeeIdD: Int,

    @Json(name = "IdPracownikM")
    val employeeIdM: Int
)
