package io.github.wulkanowy.sdk.dictionaries

import com.google.gson.annotations.SerializedName

data class GradeCategory(

    @SerializedName("Id")
    var id: Int,

    @SerializedName("Kod")
    var code: String,

    @SerializedName("Nazwa")
    var name: String
)
