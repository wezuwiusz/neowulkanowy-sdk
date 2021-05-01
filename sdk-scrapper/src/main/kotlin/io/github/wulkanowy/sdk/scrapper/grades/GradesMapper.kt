package io.github.wulkanowy.sdk.scrapper.grades

import io.github.wulkanowy.sdk.scrapper.getGradePointPercent
import io.github.wulkanowy.sdk.scrapper.getGradeShortValue
import java.util.Locale

private val pointGradeRegex = "\\d+\\.?\\d+/\\d+".toRegex()

private fun String.isEntryContainsCommentWithGrade() = isGradeValid(removeSurrounding("(", ")"))

fun GradesResponse.mapGradesList() = gradesWithSubjects.map { gradesSubject ->
    gradesSubject.grades.map { grade ->
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
            color = if ("0" == grade.color) "000000" else grade.color.toInt().toString(16).toUpperCase(),
            symbol = grade.symbol.orEmpty(),
            description = grade.description.orEmpty(),
            weightValue = if (isGradeValid(gradeEntryWithoutComment)) grade.weightValue else .0,
            teacher = grade.teacher
        ).apply {
            subject = gradesSubject.name
            comment = gradeEntryWithoutComment.run {
                when {
                    length > 4 -> grade.entry
                    startsWith("(") && endsWith(")") -> "" // getGrade_onlyGradeInCommentEntry
                    else -> grade.entry.substringBeforeLast(")").substringAfter(" (")
                }
            }
            if (comment.removeSurrounding("(", ")") == entry) comment = "" // getGrade_onlyCommentEntry
            value = gradeValue
            modifier = gradeModifier
            weight = String.format(Locale.FRANCE, "%.2f", grade.weightValue)
            date = grade.privateDate ?: GradeDate()
        }
    }
}.flatten().sortedByDescending { it.date }

fun GradesResponse.mapGradesSummary() = gradesWithSubjects.map { subject ->
    GradeSummary(
        visibleSubject = subject.visibleSubject,
        order = subject.order,
        name = subject.name,
        average = subject.average,
        predicted = getGradeShortValue(subject.proposed),
        final = getGradeShortValue(subject.annual),
        pointsSum = subject.pointsSum.takeIf { it != "-" }.orEmpty(),
        proposedPoints = subject.proposedPoints.orEmpty(),
        finalPoints = subject.finalPoints.orEmpty()
    )
}.sortedBy { it.name }.toList()

fun List<GradesStatisticsSemester>.mapGradesStatisticsSemester() = map {
    it.copy(
        items = it.items.orEmpty().reversed().mapIndexed { index, item ->
            item.copy().apply {
                grade = index + 1
                isStudentHere = item.description.contains("Tu jesteś")
            }
        }.reversed()
    )
}

fun List<GradesStatisticsPartial>.mapGradesStatisticsPartial() = map {
    it.copy(
        classSeries = it.classSeries.addGradeValue(),
        studentSeries = it.studentSeries.addGradeValue()
    )
}

private fun GradeStatisticsPartialSeries.addGradeValue(): GradeStatisticsPartialSeries {
    return copy(items = items.orEmpty().reversed().mapIndexed { i, item ->
        item.copy().apply {
            grade = i + 1
        }
    }.reversed())
}
