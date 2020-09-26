package io.github.wulkanowy.sdk.scrapper.home

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GovernmentMember(

    @Json(name = "Name")
    val name: String,

    @Json(name = "Position")
    val position: String,

    @Json(name = "Division")
    val division: String,

    @Json(name = "Id")
    val id: Int
)
