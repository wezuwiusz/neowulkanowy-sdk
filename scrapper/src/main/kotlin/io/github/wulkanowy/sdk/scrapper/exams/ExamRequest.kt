package io.github.wulkanowy.sdk.scrapper.exams

import com.google.gson.annotations.SerializedName
import java.util.Date

data class ExamRequest(

    @SerializedName("data")
    val date: Date,

    @SerializedName("rokSzkolny")
    val schoolYear: Int
)
