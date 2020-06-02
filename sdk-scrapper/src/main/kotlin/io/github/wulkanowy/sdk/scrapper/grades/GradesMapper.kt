package io.github.wulkanowy.sdk.scrapper.grades

import io.github.wulkanowy.sdk.scrapper.getGradePointPercent
import io.github.wulkanowy.sdk.scrapper.getGradeShortValue
import java.util.Locale

private val pointGradeRegex = "\\d+\\.?\\d+/\\d+".toRegex()

private fun String.isEntryContainsCommentWithGrade() = isGradeValid(removeSurrounding("(", ")"))

fun GradesResponse.mapGradesList(): List<Grade> {
    return gradesWithSubjects.map { gradesSubject ->
        gradesSubject.grades.map { grade ->
            val values = getGradeValueWithModifier(grade.entry)
            val gradeEntryWithoutComment = grade.entry.substringBefore(" (")

            Grade().apply {
                subject = gradesSubject.name
                entry = gradeEntryWithoutComment.run {
                    when {
                        isPoints && matches(pointGradeRegex) -> getGradePointPercent()
                        isEntryContainsCommentWithGrade() -> this // getGrade_onlyGradeInCommentEntry
                        removeSurrounding("(", ")").length > 4 -> "..." // getGrade_onlyCommentEntry
                        else -> removeSurrounding("(", ")")
                    }
                }
                comment = gradeEntryWithoutComment.run {
                    when {
                        length > 4 -> grade.entry
                        startsWith("(") && endsWith(")") -> "" // getGrade_onlyGradeInCommentEntry
                        else -> grade.entry.substringBeforeLast(")").substringAfter(" (")
                    }
                }
                if (comment.removeSurrounding("(", ")") == entry) comment = "" // getGrade_onlyCommentEntry
                value = values.first
                modifier = values.second
                color = if ("0" == grade.color) "000000" else grade.color.toInt().toString(16).toUpperCase()
                symbol = grade.symbol.orEmpty()
                description = grade.description.orEmpty()
                weight = String.format(Locale.FRANCE, "%.2f", grade.weightValue)
                weightValue = if (isGradeValid(gradeEntryWithoutComment)) grade.weightValue else .0
                date = grade.privateDate
                teacher = grade.teacher
            }
        }
    }.flatten().sortedByDescending { it.date }
}

fun GradesResponse.mapGradesSummary(): List<GradeSummary> {
    return gradesWithSubjects.map { subject ->
        GradeSummary().apply {
            visibleSubject = subject.visibleSubject
            order = subject.order
            name = subject.name
            average = subject.average
            predicted = getGradeShortValue(subject.proposed)
            final = getGradeShortValue(subject.annual)
            pointsSum = subject.pointsSum.takeIf { it != "-" }.orEmpty()
            proposedPoints = subject.proposedPoints.orEmpty()
            finalPoints = subject.finalPoints.orEmpty()
        }
    }.sortedBy { it.name }.toList()
}

fun List<GradesStatisticsResponse.Annual>.mapGradesStatisticsAnnual(semesterId: Int): List<GradeStatistics> {
    return map { annualSubject ->
        annualSubject.items?.reversed()?.mapIndexed { index, item ->
            item.apply {
                this.semesterId = semesterId
                gradeValue = index + 1
                grade = item.gradeValue.toString()
                subject = annualSubject.subject
            }
        }.orEmpty()
    }.flatten().reversed()
}

fun List<GradesStatisticsResponse.Partial>.mapGradesStatisticsPartial(semesterId: Int): List<GradeStatistics> {
    return map { partialSubject ->
        partialSubject.classSeries.items?.reversed()?.mapIndexed { index, item ->
            item.apply {
                this.semesterId = semesterId
                gradeValue = index + 1
                grade = item.gradeValue.toString()
                subject = partialSubject.subject
            }
        }?.reversed().orEmpty()
    }.flatten()
}

fun List<GradePointsSummary>.mapGradesStatisticsPoints(semesterId: Int): List<GradePointsSummary> {
    return map {
        it.copy(semesterId = semesterId)
    }
}
