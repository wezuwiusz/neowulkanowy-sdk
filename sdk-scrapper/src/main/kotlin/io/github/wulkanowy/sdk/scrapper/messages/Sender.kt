package io.github.wulkanowy.sdk.scrapper.messages

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Sender(

    @Json(name = "Id")
    val id: String? = null,

    @Json(name = "Name")
    val name: String? = null,

    @Json(name = "IdLogin")
    val loginId: Int? = null,

    @Json(name = "UnitId")
    val reportingUnitId: Int? = null,

    @Json(name = "Role")
    val role: Int? = null,

    @Json(name = "Hash")
    val hash: String? = null
)
