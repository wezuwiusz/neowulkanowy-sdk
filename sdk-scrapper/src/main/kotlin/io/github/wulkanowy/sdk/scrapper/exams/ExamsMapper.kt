package io.github.wulkanowy.sdk.scrapper.exams

import io.github.wulkanowy.sdk.scrapper.toLocalDate
import java.time.LocalDate

fun List<ExamResponse>.mapExamsList(startDate: LocalDate, endDate: LocalDate?): List<Exam> {
    val end = endDate ?: startDate.plusDays(4)
    return asSequence().map { weeks ->
        weeks.weeks.map { day ->
            day.exams.map { exam ->
                val teacherAndSymbol = exam.teacher.split(" [")
                exam.copy(
                    teacher = teacherAndSymbol.first(),
                    subject = exam.subject,
                    type = when (exam.type) {
                        "1" -> "Sprawdzian"
                        "2" -> "Kartkówka"
                        else -> "Praca klasowa"
                    }
                ).apply {
                    date = day.date

                    teacherSymbol = teacherAndSymbol.last().removeSuffix("]")
                }
            }
        }.flatten()
    }.flatten().filter {
        it.date.toLocalDate() >= startDate && it.date.toLocalDate() <= end
    }.sortedBy { it.date }.toList()
}
