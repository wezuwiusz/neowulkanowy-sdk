package io.github.wulkanowy.sdk.mapper

import io.github.wulkanowy.sdk.scrapper.grades.GradePointsSummary
import io.github.wulkanowy.sdk.scrapper.grades.GradeStatistics as ScrapperGradeStatistics
import io.github.wulkanowy.sdk.pojo.GradeStatistics
import io.github.wulkanowy.sdk.mobile.dictionaries.Dictionaries
import io.github.wulkanowy.sdk.mobile.grades.GradesSummaryResponse
import io.github.wulkanowy.sdk.pojo.GradePointsStatistics
import io.github.wulkanowy.sdk.pojo.GradeSummary
import io.github.wulkanowy.sdk.scrapper.grades.GradeSummary as ScrapperGradeSummary

fun GradesSummaryResponse.mapGradesSummary(dict: Dictionaries): List<GradeSummary> {
    return average.union(predicted).union(evaluative).map { it.subjectId }.distinct().sorted().map { subjectId ->
        GradeSummary(
            name = dict.subjects.singleOrNull { it.id == subjectId }?.name.orEmpty(),
            predicted = predicted.singleOrNull { it.subjectId == subjectId }?.entry.orEmpty(),
            final = evaluative.singleOrNull { it.subjectId == subjectId }?.entry.orEmpty(),
            average = (average.singleOrNull { it.subjectId == subjectId }?.average ?: "0").replace(",", ".").takeIf { it.isNotBlank() }?.toDouble() ?: 0.0,
            pointsSum = average.singleOrNull { it.subjectId == subjectId }?.pointsSum.orEmpty(),
            proposedPoints = "",
            finalPoints = ""
        )
    }
}

fun List<ScrapperGradeSummary>.mapGradesSummary(): List<GradeSummary> {
    return map {
        GradeSummary(
            name = it.name,
            finalPoints = it.finalPoints,
            proposedPoints = it.proposedPoints,
            pointsSum = it.pointsSum,
            average = it.average,
            final = it.final,
            predicted = it.predicted
        )
    }
}

fun List<ScrapperGradeStatistics>.mapGradeStatistics(): List<GradeStatistics> {
    return map {
        GradeStatistics(
            semesterId = it.semesterId,
            subject = it.subject,
            grade = it.grade,
            gradeValue = it.gradeValue,
            amount = it.amount ?: 0
        )
    }
}

fun List<GradePointsSummary>.mapGradePointsStatistics(): List<GradePointsStatistics> {
    return map {
        GradePointsStatistics(
            semesterId = it.semesterId,
            subject = it.subject,
            student = it.student,
            others = it.others
        )
    }
}
