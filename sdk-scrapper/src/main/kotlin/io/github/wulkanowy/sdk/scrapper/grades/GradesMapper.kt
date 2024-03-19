package io.github.wulkanowy.sdk.scrapper.grades

import io.github.wulkanowy.sdk.scrapper.getGradePointPercent
import io.github.wulkanowy.sdk.scrapper.getGradeShortValue
import java.util.Locale

private val pointGradeRegex = "\\d+\\.?\\d+/\\d+".toRegex()

private fun String.isEntryContainsCommentWithGrade() = isGradeValid(removeSurrounding("(", ")"))

internal fun GradesResponse.mapGradesList() = gradesWithSubjects.map { gradesSubject ->
    gradesSubject.grades.orEmpty().map { grade ->
        val (gradeValue, gradeModifier) = getGradeValueWithModifier(grade.entry)
        val gradeEntryWithoutComment = grade.entry.substringBefore(" (")

        grade.copy(
            entry = gradeEntryWithoutComment.run {
                when {
                    isPoints && matches(pointGradeRegex) -> getGradePointPercent()
                    isEntryContainsCommentWithGrade() -> this // getGrade_onlyGradeInCommentEntry
                    removeSurrounding("(", ")").length > 4 -> "..." // getGrade_onlyCommentEntry
                    else -> removeSurrounding("(", ")")
                }
            },
            symbol = grade.symbol.orEmpty(),
            description = grade.description.orEmpty(),
            weightValue = if (isGradeValid(gradeEntryWithoutComment)) grade.weightValue else .0,
            teacher = grade.teacher,
        ).apply {
            colorHex = if (0 == grade.color) "000000" else grade.color.toString(16).uppercase()
            subject = gradesSubject.name
            comment = gradeEntryWithoutComment.run {
                when {
                    length > 4 -> grade.entry
                    startsWith("(") && endsWith(")") -> "" // getGrade_onlyGradeInCommentEntry
                    else -> grade.entry.substringBeforeLast(")").substringAfter(" (")
                }
            }
            if (comment.removeSurrounding("(", ")") == entry) comment = "" // getGrade_onlyCommentEntry
            value = gradeValue ?: 0
            modifier = gradeModifier ?: 0.0
            weight = String.format(Locale.FRANCE, "%.2f", grade.weightValue)
            date = grade.privateDate
        }
    }
}.flatten().sortedByDescending { it.date }

internal fun GradesResponse.mapGradesSummary() = gradesWithSubjects.map { subject ->
    GradeSummary(
        visibleSubject = subject.visibleSubject,
        order = subject.order,
        name = subject.name.trim(),
        average = subject.average,
        predicted = getGradeShortValue(subject.proposed),
        final = getGradeShortValue(subject.annual),
        pointsSum = subject.pointsSum.takeIf { it != "-" }.orEmpty().trim(),
        proposedPoints = subject.proposedPoints.orEmpty().trim(),
        finalPoints = subject.finalPoints.orEmpty().trim(),
    )
}.sortedBy { it.name }.toList()

internal fun List<GradesStatisticsSemester>.mapGradesStatisticsSemester() = map {
    it.copy(
        items = it.items.orEmpty().reversed().mapIndexed { index, item ->
            item.copy().apply {
                grade = index + 1
                isStudentHere = item.description.contains("Tu jesteś")
            }
        }.reversed(),
    )
}

internal fun List<GradesStatisticsPartial>.mapGradesStatisticsPartial() = map {
    it.copy(
        classSeries = it.classSeries.addGradeValue(),
        studentSeries = it.studentSeries.addGradeValue(),
    )
}

private fun GradeStatisticsPartialSeries.addGradeValue(): GradeStatisticsPartialSeries {
    return copy(
        items = items.orEmpty().reversed().mapIndexed { i, item ->
            item.copy().apply {
                grade = i + 1
            }
        }.reversed(),
    )
}
