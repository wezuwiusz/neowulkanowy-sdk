package io.github.wulkanowy.sdk.mobile.dictionaries

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AttendanceType(

    @Json(name = "Id")
    val id: Int,

    @Json(name = "Symbol")
    val symbol: String,

    @Json(name = "Nazwa")
    val name: String,

    @Json(name = "Aktywny")
    val active: Boolean,

    @Json(name = "WpisDomyslny")
    val defaultEntry: Boolean,

    @Json(name = "IdKategoriaFrek")
    val categoryId: Int,
)
