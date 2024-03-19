package io.github.wulkanowy.sdk.scrapper.conferences

import io.github.wulkanowy.sdk.scrapper.adapter.CustomDateAdapter
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
    @JsonNames("opis")
    val topic: String,

    @SerialName("Agenda")
    val agenda: String = "",

    @SerialName("ObecniNaZebraniu")
    @JsonNames("obecniNaZebraniu")
    val presentOnConference: String,

    @SerialName("ZebranieOnline")
    @JsonNames("zebranieOnline")
    val online: String?,

    @SerialName("Id")
    @JsonNames("id")
    val id: Int,

    @JsonNames("dataCzas")
    @Serializable(with = CustomDateAdapter::class)
    val date: LocalDateTime = LocalDateTime.now(),
)
