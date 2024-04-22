package io.github.wulkanowy.sdk.pojo

import java.time.LocalDate

data class Grades(
    val details: List<Grade>,
    val summary: List<GradeSummary>,
    val descriptive: List<GradeDescriptive>,
    val isAverage: Boolean,
    val isPoints: Boolean,
    val isForAdults: Boolean,
    val type: Int,
)

data class Grade(
    val subject: String,
    val entry: String,
    val value: Double,
    val modifier: Double,
    val weight: String,
    val weightValue: Double,
    val comment: String,
    val symbol: String,
    val description: String,
    val color: String,
    val teacher: String,
    val date: LocalDate,
//    val counter: Int?,
//    val denominator: Int?,
//    val creationDate: Int,
//    val modificationDate: Int
)

data class GradeSummary(
    val name: String,
    val average: Double = .0,
    val averageAllYear: Double? = null,
    val predicted: String,
    val final: String,
    val pointsSum: String,
    val pointsSumAllYear: String? = null,
    val proposedPoints: String,
    val finalPoints: String,
)

data class GradeDescriptive(
    val subject: String,
    val description: String,
)
