package io.github.wulkanowy.sdk.mapper

import io.github.wulkanowy.sdk.pojo.Grade
import io.github.wulkanowy.sdk.pojo.GradeSummary
import io.github.wulkanowy.sdk.pojo.Grades
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import io.github.wulkanowy.sdk.hebe.models.Grade as HebeGrade
import io.github.wulkanowy.sdk.scrapper.grades.Grade as ScrapperGrade
import io.github.wulkanowy.sdk.scrapper.grades.GradeSummary as ScrapperGradeSummary
import io.github.wulkanowy.sdk.scrapper.grades.Grades as ScrapperGrades

internal fun List<ScrapperGrade>.mapGradesDetails() = map {
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

internal fun List<ScrapperGradeSummary>.mapGradesSummary() = map {
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

internal fun ScrapperGrades.mapGrades() = Grades(
    details = details.mapGradesDetails(),
    summary = summary.mapGradesSummary(),
    isAverage = isAverage,
    isPoints = isPoints,
    isForAdults = isForAdults,
    type = type,
)

internal fun List<HebeGrade>.mapGrades() = Grades(
    details = map {
        Grade(
            subject = it.column.subject.name,
            entry = it.content,
            value = it.value ?: 0.0,
            modifier = 0.0,
            weight = it.column.weight.toString(),
            weightValue = it.column.weight,
            comment = it.comment,
            symbol = it.column.code,
            description = it.column.name,
            color = it.column.color.toString(16).uppercase(),
            teacher = "${it.creator.name} ${it.creator.surname}",
            date = LocalDate.parse(it.dateCreated.date, DateTimeFormatter.ofPattern("yyyy.MM.dd")),
        )
    },
    summary = listOf(),
    isAverage = false,
    isPoints = false,
    isForAdults = false,
    type = 0,
)
