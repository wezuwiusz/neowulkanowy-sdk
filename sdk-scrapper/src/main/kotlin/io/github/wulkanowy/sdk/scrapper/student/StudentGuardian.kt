package io.github.wulkanowy.sdk.scrapper.student

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class StudentGuardian(

    @SerialName("Id")
    val id: Int = -1,

    @SerialName("Imie")
    @JsonNames("imie")
    val name: String,

    @SerialName("Nazwisko")
    @JsonNames("nazwisko")
    val lastName: String,

    @SerialName("StPokrewienstwa")
    @JsonNames("stPokrewienstwa")
    val kinship: String?,

    @SerialName("Adres")
    @JsonNames("adres")
    val address: String,

    @SerialName("TelDomowy")
    @JsonNames("telDomowy")
    val homePhone: String?,

    @SerialName("TelKomorkowy")
    @JsonNames("telKomorkowy")
    val cellPhone: String?,

    @SerialName("TelSluzbowy")
    @JsonNames("telSluzbowy")
    val workPhone: String?,

    @SerialName("Email")
    @JsonNames("email")
    val email: String?,

    @SerialName("FullName")
    val fullName: String = "",

    @SerialName("Telefon")
    val phone: String = "",
)
