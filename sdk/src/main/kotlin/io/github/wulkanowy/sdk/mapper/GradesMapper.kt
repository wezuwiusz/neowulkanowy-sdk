package io.github.wulkanowy.sdk.mapper

import io.github.wulkanowy.sdk.pojo.Grade
import io.github.wulkanowy.sdk.pojo.GradeDescriptive
import io.github.wulkanowy.sdk.pojo.GradeSummary
import io.github.wulkanowy.sdk.pojo.Grades
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import io.github.wulkanowy.sdk.hebe.models.Grade as HebeGrade
import io.github.wulkanowy.sdk.hebe.models.GradeAverage as HebeGradeAverage
import io.github.wulkanowy.sdk.hebe.models.GradeSummary as HebeGradeSummary
import io.github.wulkanowy.sdk.scrapper.grades.Grade as ScrapperGrade
import io.github.wulkanowy.sdk.scrapper.grades.GradeDescriptive as ScrapperGradeDescriptive
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
        pointsSumAllYear = it.pointsSumAllYear,
        average = it.average,
        averageAllYear = it.averageAllYear,
        final = it.final,
        predicted = it.predicted,
    )
}

internal fun ScrapperGrades.mapGrades() = Grades(
    details = details.mapGradesDetails(),
    summary = summary.mapGradesSummary(),
    descriptive = descriptive.mapDescriptive(),
    isAverage = isAverage,
    isPoints = isPoints,
    isForAdults = isForAdults,
    type = type,
)

internal fun List<ScrapperGradeDescriptive>.mapDescriptive() = map {
    GradeDescriptive(
        subject = it.subject,
        description = it.description.orEmpty(),
    )
}

internal fun Triple<List<HebeGrade>, List<HebeGradeSummary>, List<HebeGradeAverage>>.mapGrades() = Grades(
    details = first.map { grade ->
        Grade(
            subject = grade.column.subject.name,
            entry = grade.content,
            value = grade.value ?: 0.0,
            modifier = 0.0,
            weight = String.format("%.2f", grade.column.weight),
            weightValue = grade.column.weight,
            comment = grade.comment,
            symbol = grade.column.code,
            description = grade.column.name,
            color = grade.column.color.toString(16).uppercase(),
            teacher = "${grade.creator.name} ${grade.creator.surname}",
            date = LocalDate.parse(grade.dateCreated.date, DateTimeFormatter.ofPattern("yyyy-MM-dd")),
        )
    },
    summary = second.map { summary ->
        GradeSummary(
            name = summary.subject.name,
            average = third.find { it.id == summary.id }
                ?.average?.replace(",", ".")
                ?.toDoubleOrNull() ?: .0,
            predicted = summary.entry1.orEmpty(),
            final = summary.entry2.orEmpty(),
            pointsSum = "",
            proposedPoints = "",
            finalPoints = "",
        )
    },
    descriptive = emptyList(),
    isAverage = third.isNotEmpty(),
    isPoints = false,
    isForAdults = false,
    type = 0,
)
