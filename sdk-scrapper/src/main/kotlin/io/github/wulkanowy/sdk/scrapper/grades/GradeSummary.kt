package io.github.wulkanowy.sdk.scrapper.grades

data class GradeSummary(
    val order: Int = 0,
    val name: String = "",
    val average: Double = .0,
    val averageAllYear: Double? = null,
    val predicted: String = "",
    val final: String = "",
    val pointsSum: String = "",
    val pointsSumAllYear: String? = null,
    val proposedPoints: String = "",
    val finalPoints: String = "",
    val visibleSubject: Boolean = false,
)
