package io.github.wulkanowy.sdk.scrapper.grades

import com.google.gson.annotations.SerializedName

data class GradeStatistics(

    @SerializedName("Value")
    val amount: Int? = 0
) {

    var semesterId: Int = 0

    lateinit var subject: String

    lateinit var grade: String

    var gradeValue: Int = 0
}
