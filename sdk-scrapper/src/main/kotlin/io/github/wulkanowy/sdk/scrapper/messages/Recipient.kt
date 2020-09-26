package io.github.wulkanowy.sdk.scrapper.messages

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Recipient(

    @Json(name = "Id")
    val id: String,

    @Json(name = "Name")
    val name: String,

    @Json(name = "IdLogin")
    val loginId: Int,

    @Json(name = "UnitId")
    val reportingUnitId: Int?,

    @Json(name = "Role")
    val role: Int,

    @Json(name = "Hash")
    val hash: String,

    @Transient
    val shortName: String? = ""
)
