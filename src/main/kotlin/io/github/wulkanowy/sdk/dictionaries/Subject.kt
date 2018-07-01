package io.github.wulkanowy.sdk.dictionaries

import com.google.gson.annotations.SerializedName

data class Subject(

    @SerializedName("Id")
    var id: Int,

    @SerializedName("Nazwa")
    var name: String,

    @SerializedName("Kod")
    var code: String,

    @SerializedName("Aktywny")
    var active: Boolean,

    @SerializedName("Pozycja")
    var position: Int
)
