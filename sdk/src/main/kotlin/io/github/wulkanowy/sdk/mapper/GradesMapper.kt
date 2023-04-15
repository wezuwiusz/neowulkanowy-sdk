package io.github.wulkanowy.sdk.mapper

import io.github.wulkanowy.sdk.pojo.Grade
import io.github.wulkanowy.sdk.pojo.GradeSummary
import io.github.wulkanowy.sdk.pojo.Grades
import io.github.wulkanowy.sdk.scrapper.grades.Grade as ScrapperGrade
import io.github.wulkanowy.sdk.scrapper.grades.GradeSummary as ScrapperGradeSummary
import io.github.wulkanowy.sdk.scrapper.grades.Grades as ScrapperGrades

fun List<ScrapperGrade>.mapGradesDetails() = map {
    Grade(
        subject = it.subject,
        description = it.description.orEmpty(),
        symbol = it.symbol.orEmpty(),
        comment = it.comment,
        date = it.date,
        teacher = it.teacher,
        entry = it.entry,
        weight = it.weight,
        weightValue = it.weightValue,
        color = it.colorHex,
        value = it.value.toDouble(),
        modifier = it.modifier,
    )
}

fun List<ScrapperGradeSummary>.mapGradesSummary() = map {
    GradeSummary(
        name = it.name,
        finalPoints = it.finalPoints,
        proposedPoints = it.proposedPoints,
        pointsSum = it.pointsSum,
        average = it.average,
        final = it.final,
        predicted = it.predicted,
    )
}

fun ScrapperGrades.mapGrades() = Grades(
    details = details.mapGradesDetails(),
    summary = summary.mapGradesSummary(),
    isAverage = isAverage,
    isPoints = isPoints,
    isForAdults = isForAdults,
    type = type,
)
