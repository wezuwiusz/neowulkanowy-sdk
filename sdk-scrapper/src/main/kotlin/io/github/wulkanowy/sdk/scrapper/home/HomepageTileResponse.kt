package io.github.wulkanowy.sdk.scrapper.home

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class HomepageTileResponse(

    @Json(name = "IkonkaNazwa")
    val iconName: String?,

    @Json(name = "Num")
    val number: Int?,

    @Json(name = "Zawartosc")
    val content: List<HomepageTileResponse>,

    @Json(name = "Nazwa")
    val name: String,

    @Json(name = "Url")
    val url: String?,

    @Json(name = "Dane")
    val data: String?,

    @Json(name = "Symbol")
    val symbol: String?,

    @Json(name = "Nieaktywny")
    val inactive: Boolean
)
