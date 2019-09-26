package io.github.wulkanowy.api.grades

import io.github.wulkanowy.api.getGradePointPercent
import io.github.wulkanowy.api.getGradeShortValue
import java.util.Locale

fun GradesResponse.mapGradesList(): List<Grade> {
    return gradesWithSubjects.map { gradesSubject ->
        gradesSubject.grades.map { grade ->
            val values = getGradeValueWithModifier(grade.entry)
            grade.apply {
                subject = gradesSubject.name
                comment = entry.substringBefore(" (").run {
                    if (length > 4) this
                    else entry.substringBeforeLast(")").substringAfter(" (")
                }
                entry = entry.substringBefore(" (").run {
                    if (isPoints && matches("\\d+/\\d+".toRegex())) getGradePointPercent()
                    else if (length > 4) "..."
                    else this
                }
                if (comment == entry) comment = ""
                value = values.first
                date = privateDate
                modifier = values.second
                weight = String.format(Locale.FRANCE, "%.2f", weightValue)
                weightValue = if (isGradeValid(entry)) weightValue else .0
                color = if ("0" == color) "000000" else color.toInt().toString(16).toUpperCase()
                symbol = symbol ?: ""
                description = description ?: ""
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
            pointsSum = subject.pointsSum.orEmpty()
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
