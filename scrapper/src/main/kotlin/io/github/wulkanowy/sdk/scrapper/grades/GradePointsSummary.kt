package io.github.wulkanowy.sdk.scrapper.grades

import com.google.gson.annotations.SerializedName

data class GradePointsSummary(

    val semesterId: Int = 0,

    @SerializedName("Subject")
    val subject: String,

    @SerializedName("Value1")
    val others: Double,

    @SerializedName("Value2")
    val student: Double
)
