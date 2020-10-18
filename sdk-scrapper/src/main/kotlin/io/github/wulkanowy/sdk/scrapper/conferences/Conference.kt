package io.github.wulkanowy.sdk.scrapper.conferences

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.time.LocalDateTime

@JsonClass(generateAdapter = true)
data class Conference(

    @Json(name = "Tytul")
    val title: String,

    @Json(name = "TematZebrania")
    val subject: String,

    @Json(name = "Agenda")
    val agenda: String,

    @Json(name = "ObecniNaZebraniu")
    val presentOnConference: String,

    @Json(name = "ZebranieOnline")
    val online: Any?,

    @Json(name = "Id")
    val id: Int,

    @Transient
    val date: LocalDateTime = LocalDateTime.now()
)
