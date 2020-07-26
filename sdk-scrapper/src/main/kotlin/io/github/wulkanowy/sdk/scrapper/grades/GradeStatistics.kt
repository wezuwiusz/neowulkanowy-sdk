package io.github.wulkanowy.sdk.scrapper.grades

import com.google.gson.annotations.SerializedName

class GradeStatistics {

    var semesterId: Int = 0

    lateinit var subject: String

    lateinit var grade: String

    var gradeValue: Int = 0

    @SerializedName("Value")
    var amount: Int? = 0
}
