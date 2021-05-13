package io.github.wulkanowy.sdk.scrapper.student

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StudentGuardian(

    @Json(name = "Id")
    val id: Int,

    @Json(name = "Imie")
    val name: String,

    @Json(name = "Nazwisko")
    val lastName: String,

    @Json(name = "StPokrewienstwa")
    val kinship: String?,

    @Json(name = "Adres")
    val address: String,

    @Json(name = "TelDomowy")
    val homePhone: String?,

    @Json(name = "TelKomorkowy")
    val cellPhone: String?,

    @Json(name = "TelSluzbowy")
    val workPhone: String?,

    @Json(name = "Email")
    val email: String?,

    @Json(name = "FullName")
    val fullName: String,

    @Json(name = "Telefon")
    val phone: String
)
