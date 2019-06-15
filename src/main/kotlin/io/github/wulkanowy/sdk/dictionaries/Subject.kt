package io.github.wulkanowy.sdk.dictionaries

import com.google.gson.annotations.SerializedName

data class Subject(

    @SerializedName("Id")
    val id: Int,

    @SerializedName("Nazwa")
    val name: String,

    @SerializedName("Kod")
    val code: String,

    @SerializedName("Aktywny")
    val active: Boolean,

    @SerializedName("Pozycja")
    val position: Int
)
