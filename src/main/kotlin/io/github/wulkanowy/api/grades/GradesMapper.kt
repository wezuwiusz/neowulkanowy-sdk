package io.github.wulkanowy.api.grades

import kotlin.String
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
                entry = entry.substringBefore(" (").run { if (length > 4) "..." else this }
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
