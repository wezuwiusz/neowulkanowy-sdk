package io.github.wulkanowy.sdk.scrapper.grades

import com.google.gson.annotations.SerializedName
import pl.droidsonroids.jspoon.annotation.Selector

class GradeStatistics {

    var semesterId: Int = 0

    @Selector("td", index = 0)
    lateinit var subject: String

    @Selector("td", index = 1, regex = "^([^,]+)")
    lateinit var grade: String

    var gradeValue: Int = 0

    @SerializedName("Value")
    @Selector("td:last-of-type", regex = "Klasa ocen: ([^,]+)")
    var amount: Int? = 0
}
