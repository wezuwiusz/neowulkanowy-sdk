package io.github.wulkanowy.sdk.grades

import io.github.wulkanowy.sdk.dictionaries.Dictionaries
import io.github.wulkanowy.sdk.pojo.GradeSummary
import io.github.wulkanowy.api.grades.GradeSummary as ScrapperGradeSummary
import io.github.wulkanowy.sdk.grades.GradeSummary as ApiGradeSummary

fun GradesSummaryResponse.mapGradesSummary(dict: Dictionaries): List<GradeSummary> {
    return average.union(predicted).union(evaluative).map { it.subjectId }.distinct().sorted().map { subjectId ->
        GradeSummary(
            name = dict.subjects.singleOrNull { it.id == subjectId }?.name.orEmpty(),
            predicted = predicted.singleOrNull { it.subjectId == subjectId }?.entry.orEmpty(),
            final = evaluative.singleOrNull { it.subjectId == subjectId }?.entry.orEmpty(),
            average = (average.singleOrNull { it.subjectId == subjectId }?.average ?: "0").replace(",", ".").toDouble(),
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
