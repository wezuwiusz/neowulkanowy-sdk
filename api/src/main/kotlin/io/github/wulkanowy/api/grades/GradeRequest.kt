package io.github.wulkanowy.api.grades

import com.google.gson.annotations.SerializedName

data class GradeRequest(

    @SerializedName("okres")
    val semesterId: Int?
)
