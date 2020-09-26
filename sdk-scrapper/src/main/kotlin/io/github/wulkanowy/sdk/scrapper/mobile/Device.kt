package io.github.wulkanowy.sdk.scrapper.mobile

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.Date

@JsonClass(generateAdapter = true)
data class Device(

    @Json(name = "Id")
    val id: Int = 0,

    @Json(name = "IdentyfikatorUrzadzenia")
    val deviceId: String? = null,

    @Json(name = "NazwaUrzadzenia")
    val name: String? = null,

    @Json(name = "DataUtworzenia")
    val createDate: Date? = null,

    @Json(name = "DataModyfikacji")
    val modificationDate: Date? = null
)
