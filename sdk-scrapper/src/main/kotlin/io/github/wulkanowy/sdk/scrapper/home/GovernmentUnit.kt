package io.github.wulkanowy.sdk.scrapper.home

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GovernmentUnit(

    @Json(name = "UnitName")
    val unitName: String,

    @Json(name = "People")
    val people: List<GovernmentMember>
)
