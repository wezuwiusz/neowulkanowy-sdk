package io.github.wulkanowy.api.grades

import pl.droidsonroids.jspoon.annotation.Selector

class GradeStatistics {

    var semesterId: Int = 0

    @Selector("td", index = 0)
    lateinit var subject: String

    @Selector("td", index = 1, regex = "^([^,]+)")
    lateinit var grade: String

    var gradeValue: Int = 0

    @Selector("td:last-of-type", regex = "Klasa ocen: ([^,]+)")
    var amount: Int = 0
}
