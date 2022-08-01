package io.github.wulkanowy.sdk.mapper

import io.github.wulkanowy.sdk.mobile.dictionaries.Dictionaries
import io.github.wulkanowy.sdk.mobile.grades.GradesSummaryResponse
import io.github.wulkanowy.sdk.pojo.Grade
import io.github.wulkanowy.sdk.pojo.GradeSummary
import io.github.wulkanowy.sdk.scrapper.grades.isGradeValid
import io.github.wulkanowy.sdk.toLocalDate
import io.github.wulkanowy.sdk.mobile.grades.Grade as ApiGrade
import io.github.wulkanowy.sdk.scrapper.grades.Grade as ScrapperGrade
import io.github.wulkanowy.sdk.scrapper.grades.GradeSummary as ScrapperGradeSummary

fun List<ApiGrade>.mapGradesDetails(dict: Dictionaries) = map { grade ->
    Grade(
        subject = dict.subjects.singleOrNull { it.id == grade.subjectId }?.name.orEmpty(),
        description = grade.description,
        symbol = dict.gradeCategories.singleOrNull { it.id == grade.categoryId }?.code.orEmpty(),
        comment = grade.comment.orEmpty(),
        date = grade.creationDate.toLocalDate(),
        teacher = dict.teachers.singleOrNull { it.id == grade.employeeIdD }?.let { "${it.name} ${it.surname}" }.orEmpty(),
        entry = if (grade.entry.isNotBlank()) grade.entry else "...",
        weightValue = if (isGradeValid(grade.entry)) grade.gradeWeight else .0,
        modifier = grade.modificationWeight ?: .0,
        value = grade.value,
        weight = grade.weight,
        color = "0"
    )
}

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
        modifier = it.modifier
    )
}

fun GradesSummaryResponse.mapGradesSummary(dict: Dictionaries) = average
    .union(predicted)
    .union(evaluative)
    .map { it.subjectId }.distinct().sorted().map { subjectId ->
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

fun List<ScrapperGradeSummary>.mapGradesSummary() = map {
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

fun Pair<List<ScrapperGrade>, List<ScrapperGradeSummary>>.mapGrades() = first.mapGradesDetails() to second.mapGradesSummary()

fun Pair<List<ApiGrade>, GradesSummaryResponse>.mapGrades(dict: Dictionaries) = first.mapGradesDetails(dict) to second.mapGradesSummary(dict)
