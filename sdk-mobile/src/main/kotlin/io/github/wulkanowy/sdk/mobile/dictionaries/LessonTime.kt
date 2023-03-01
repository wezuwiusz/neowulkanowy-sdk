package io.github.wulkanowy.sdk.mobile.dictionaries

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LessonTime(

    @Json(name = "Id")
    val id: Int,

    @Json(name = "Numer")
    val number: Int,

    @Json(name = "Poczatek")
    val start: Long,

    @Json(name = "PoczatekTekst")
    val startText: String,

    @Json(name = "Koniec")
    val end: Long,

    @Json(name = "KoniecTekst")
    val endText: String,
)
