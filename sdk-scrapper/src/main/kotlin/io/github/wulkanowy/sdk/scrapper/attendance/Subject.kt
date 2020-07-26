package io.github.wulkanowy.sdk.scrapper.attendance

import com.google.gson.annotations.SerializedName

data class Subject(

    @SerializedName("Nazwa")
    var name: String = "Wszystkie",

    @SerializedName("Id")
    var value: Int = -1
)
