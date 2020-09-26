package io.github.wulkanowy.sdk.mobile.dictionaries

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AttendanceCategory(

    @Json(name = "Id")
    val id: Int,

    @Json(name = "Nazwa")
    val name: String,

    @Json(name = "Pozycja")
    val position: Int,

    @Json(name = "Obecnosc")
    val presence: Boolean,

    @Json(name = "Nieobecnosc")
    val absence: Boolean,

    @Json(name = "Zwolnienie")
    val exemption: Boolean,

    @Json(name = "Spoznienie")
    val lateness: Boolean,

    @Json(name = "Usprawiedliwione")
    val excused: Boolean,

    @Json(name = "Usuniete")
    val deleted: Boolean
)
