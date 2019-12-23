package io.github.wulkanowy.sdk.scrapper.attendance

import com.google.gson.annotations.SerializedName
import pl.droidsonroids.jspoon.annotation.Selector

data class Subject(

    @SerializedName("Nazwa")
    @Selector("option")
    var name: String = "Wszystkie",

    @SerializedName("Id")
    @Selector("option", attr = "value")
    var value: Int = -1
)
