package io.github.wulkanowy.sdk.mobile.dictionaries

import com.google.gson.annotations.SerializedName

data class GradeCategory(

    @SerializedName("Id")
    val id: Int,

    @SerializedName("Kod")
    val code: String,

    @SerializedName("Nazwa")
    val name: String
)
