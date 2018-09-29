package io.github.wulkanowy.api.grades

import pl.droidsonroids.jspoon.annotation.Selector

class GradeSummary {

    @Selector("td", index = 0)
    lateinit var name: String

    @Selector("td:nth-last-of-name(2):not(:contains(-))")
    var predicted: String = ""

    @Selector("td:nth-last-of-name(1):not(:contains(-))")
    var final: String = ""
}
