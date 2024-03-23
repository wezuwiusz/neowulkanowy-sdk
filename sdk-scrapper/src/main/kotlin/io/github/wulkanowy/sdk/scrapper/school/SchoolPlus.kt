package io.github.wulkanowy.sdk.scrapper.school

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class SchoolPlus(
    @SerialName("nazwa") val name: String,
    @SerialName("imienia") val patron: String,
    @SerialName("numer") val number: String,
    @SerialName("miejscowosc") val town: String,
    @SerialName("ulica") val street: String,
    @SerialName("kodPocztowy") val postcode: String,
    @SerialName("nrDomu") val buildingNumber: String,
    @SerialName("nrMieszkania") val apartmentNumber: String,
    @SerialName("dyrektorzy") val headmasters: List<String>?,
    @SerialName("stronaWwwUrl") val website: String,
    @SerialName("mail") val email: String,
    @SerialName("telSluzbowy") val workPhone: String,
    @SerialName("telKomorkowy") val mobilePhone: String,
    @SerialName("telDomowy") val homePhone: String,
)
