package io.github.wulkanowy.sdk.scrapper.grades

import com.google.gson.annotations.SerializedName

data class GradeRequest(

    @SerializedName("okres")
    val semesterId: Int?
)
