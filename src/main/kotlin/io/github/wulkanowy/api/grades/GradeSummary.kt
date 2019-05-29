package io.github.wulkanowy.api.grades

import pl.droidsonroids.jspoon.annotation.Selector

class GradeSummary {

    var order: Int = 0

    @Selector("td", index = 0)
    var name: String = ""

    @Selector("td:nth-last-of-type(2)")
    var predicted: String = ""

    @Selector("td:nth-last-of-type(1)")
    var final: String = ""

    var proposedPoints: String = ""

    var pointsSum: String = ""

    var average: Double = .0

    var visibleSubject: Boolean = false
}
