package io.github.wulkanowy.sdk.scrapper.home

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HomepageTileResponse(

    @SerialName("IkonkaNazwa")
    val iconName: String?,

    @SerialName("Num")
    val number: Int?,

    @SerialName("Zawartosc")
    val content: List<HomepageTileResponse>,

    @SerialName("Nazwa")
    val name: String,

    @SerialName("Url")
    val url: String?,

    @SerialName("Dane")
    val data: String?,

    @SerialName("Symbol")
    val symbol: String?,

    @SerialName("Nieaktywny")
    val inactive: Boolean
)
