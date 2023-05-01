package io.github.wulkanowy.sdk.scrapper.conferences

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.time.LocalDateTime

@Serializable
data class Conference(

    @SerialName("Tytul")
    val place: String,

    @SerialName("TematZebrania")
    val topic: String,

    @SerialName("Agenda")
    val agenda: String,

    @SerialName("ObecniNaZebraniu")
    val presentOnConference: String,

    @SerialName("ZebranieOnline")
    val online: String?,

    @SerialName("Id")
    val id: Int,

    @Transient
    val date: LocalDateTime = LocalDateTime.now(),
)
