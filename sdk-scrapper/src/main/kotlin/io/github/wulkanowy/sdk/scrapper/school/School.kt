package io.github.wulkanowy.sdk.scrapper.school

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class School(

    @Json(name = "Nazwa")
    val name: String,

    @Json(name = "Adres")
    val address: String,

    @Json(name = "Kontakt")
    val contact: String,

    @Json(name = "Dyrektor")
    val headmaster: String,

    @Json(name = "Pedagog")
    val pedagogue: String
)
