package io.github.wulkanowy.sdk.scrapper.student

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StudentGuardian(

    @SerialName("Id")
    val id: Int,

    @SerialName("Imie")
    val name: String,

    @SerialName("Nazwisko")
    val lastName: String,

    @SerialName("StPokrewienstwa")
    val kinship: String?,

    @SerialName("Adres")
    val address: String,

    @SerialName("TelDomowy")
    val homePhone: String?,

    @SerialName("TelKomorkowy")
    val cellPhone: String?,

    @SerialName("TelSluzbowy")
    val workPhone: String?,

    @SerialName("Email")
    val email: String?,

    @SerialName("FullName")
    val fullName: String,

    @SerialName("Telefon")
    val phone: String
)
