package io.github.wulkanowy.sdk.scrapper.home

import com.google.gson.annotations.SerializedName

data class HomepageTileResponse(

    @SerializedName("IkonkaNazwa")
    val iconName: String?,

    @SerializedName("Num")
    val number: Int?,

    @SerializedName("Zawartosc")
    val content: List<HomepageTileResponse>,

    @SerializedName("Nazwa")
    val name: String,

    @SerializedName("Url")
    val url: String,

    @SerializedName("Symbol")
    val symbol: String,

    @SerializedName("Nieaktywny")
    val inactive: Boolean
)
