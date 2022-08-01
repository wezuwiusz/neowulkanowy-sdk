package io.github.wulkanowy.sdk.scrapper.school

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class School(

    @SerialName("Nazwa")
    val name: String,

    @SerialName("Adres")
    val address: String,

    @SerialName("Kontakt")
    val contact: String,

    @SerialName("Dyrektor")
    val headmaster: String,

    @SerialName("Pedagog")
    val pedagogue: String
)
