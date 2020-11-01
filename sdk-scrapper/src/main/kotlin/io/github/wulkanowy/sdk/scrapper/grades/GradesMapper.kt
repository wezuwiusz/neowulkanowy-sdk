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
            date = grade.privateDate
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

fun List<GradesStatisticsAnnual>.mapGradesStatisticsAnnual() = map { annualSubject ->
    annualSubject.items?.reversed()?.mapIndexed { index, item ->
        GradeStatisticsAnnualItem(
            subject = annualSubject.subject,
            grade = index + 1,
            amount = item.value,
            isStudentHere = item.description.contains("Tu jesteś")
        )
    }.orEmpty()
}.flatten().reversed()

fun List<GradesStatisticsPartial>.mapGradesStatisticsPartial() = map { partialSubject ->
    partialSubject.let {
        GradeStatisticsSubject(
            subject = it.subject,
            average = it.classSeries.average.orEmpty(),
            items = it.classSeries.items?.reversed()?.mapIndexed { index, item ->
                GradeStatisticsPartial(
                    grade = index + 1,
                    amount = item.amount ?: 0
                )
            }?.reversed().orEmpty()
        )
    }
}
