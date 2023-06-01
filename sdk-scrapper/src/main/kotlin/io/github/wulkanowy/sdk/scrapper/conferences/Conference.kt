package io.github.wulkanowy.sdk.scrapper.conferences

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.JsonNames
import java.time.LocalDateTime

@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class Conference(

    @SerialName("Tytul")
    @JsonNames("sala")
    val place: String,

    @SerialName("TematZebrania")
    val topic: String,

    @SerialName("Agenda")
    @JsonNames("opis")
    val agenda: String,

    @SerialName("ObecniNaZebraniu")
    @JsonNames("obecniNaZebraniu")
    val presentOnConference: String,

    @SerialName("ZebranieOnline")
    @JsonNames("zebranieOnline")
    val online: String?,

    @SerialName("Id")
    @JsonNames("id")
    val id: Int,

    @Transient
    val date: LocalDateTime = LocalDateTime.now(),
)
