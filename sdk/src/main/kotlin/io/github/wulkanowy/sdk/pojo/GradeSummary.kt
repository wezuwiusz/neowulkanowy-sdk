package io.github.wulkanowy.sdk.pojo

data class GradeSummary(
    var name: String,
    var average: Double = .0,
    var predicted: String,
    var final: String,
    var pointsSum: String,
    var proposedPoints: String,
    var finalPoints: String,
)
