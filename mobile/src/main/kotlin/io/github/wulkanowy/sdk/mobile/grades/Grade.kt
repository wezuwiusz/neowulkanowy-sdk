package io.github.wulkanowy.sdk.mobile.grades

import com.google.gson.annotations.SerializedName

data class Grade(

    @SerializedName("id")
    val id: Int,

    @SerializedName("Pozycja")
    val position: Int,

    @SerializedName("PrzedmiotPozycja")
    val subjectPosition: Int,

    @SerializedName("IdPrzedmiot")
    val subjectId: Int,

    @SerializedName("IdKategoria")
    val categoryId: Int,

    @SerializedName("Wpis")
    val entry: String,

    @SerializedName("Wartosc")
    val value: Double,

    @SerializedName("WagaModyfikatora")
    val modificationWeight: Double?,

    @SerializedName("WagaOceny")
    val gradeWeight: Double,

    @SerializedName("Licznik")
    val counter: Int?,

    @SerializedName("Mianownik")
    val denominator: Int?,

    @SerializedName("Komentarz")
    val comment: String?,

    @SerializedName("Waga")
    val weight: String,

    @SerializedName("Opis")
    val description: String,

    @SerializedName("DataUtworzenia")
    val creationDate: Long,

    @SerializedName("DataUtworzeniaTekst")
    val creationDateText: String,

    @SerializedName("DataModyfikacji")
    val modificationDate: Long,

    @SerializedName("DataModyfikacjiTekst")
    val modificationDateText: String,

    @SerializedName("IdPracownikD")
    val employeeIdD: Int,

    @SerializedName("IdPracownikM")
    val employeeIdM: Int
)
