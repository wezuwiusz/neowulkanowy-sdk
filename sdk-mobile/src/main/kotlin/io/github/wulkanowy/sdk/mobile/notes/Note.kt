package io.github.wulkanowy.sdk.mobile.notes

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Note(

    @Json(name = "Id")
    val id: Int,

    @Json(name = "IdKategoriaUwag")
    val noteCategoryId: Int,

    @Json(name = "IdUczen")
    val studentId: Int,

    @Json(name = "UczenImie")
    val studentName: String,

    @Json(name = "UczenNazwisko")
    val studentSurname: String,

    @Json(name = "IdPracownik")
    val employeeId: Int,

    @Json(name = "PracownikImie")
    val employeeName: String,

    @Json(name = "PracownikNazwisko")
    val employeeSurname: String,

    @Json(name = "DataWpisu")
    val entryDate: Long,

    @Json(name = "DataWpisuTekst")
    val entryDateText: String,

    @Json(name = "DataModyfikacji")
    val modificationDate: Long?,

    @Json(name = "DataModyfikacjiTekst")
    val modificationDateText: String?,

    @Json(name = "UwagaKey")
    val noteKey: String,

    @Json(name = "TrescUwagi")
    val content: String
)
