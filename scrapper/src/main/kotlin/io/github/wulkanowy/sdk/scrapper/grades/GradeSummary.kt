package io.github.wulkanowy.sdk.scrapper.grades

import pl.droidsonroids.jspoon.annotation.Selector

class GradeSummary {

    var order: Int = 0

    @Selector("td", index = 0)
    var name: String = ""

    var average: Double = .0

    @Selector("td:nth-last-of-type(2)")
    var predicted: String = ""

    @Selector("td:nth-last-of-type(1)")
    var final: String = ""

    var pointsSum: String = ""

    var proposedPoints: String = ""

    var finalPoints: String = ""

    var visibleSubject: Boolean = false
}
