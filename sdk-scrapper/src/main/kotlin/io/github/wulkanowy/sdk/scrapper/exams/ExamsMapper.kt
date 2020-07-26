package io.github.wulkanowy.sdk.scrapper.exams

import io.github.wulkanowy.sdk.scrapper.toLocalDate
import java.time.LocalDate

fun List<ExamResponse>.mapExamsList(startDate: LocalDate, endDate: LocalDate?): List<Exam> {
    val end = endDate ?: startDate.plusDays(4)
    return asSequence().map { weeks ->
        weeks.weeks.map { day ->
            day.exams.map { exam ->
                exam.copy(
                    teacher = exam.teacher.split(" [").first(),
                    subject = exam.subject.substringBeforeLast(" "),
                    type = when (exam.type) {
                        "1" -> "Sprawdzian"
                        "2" -> "Kartkówka"
                        else -> "Praca klasowa"
                    }
                ).apply {
                    date = day.date

                    teacherSymbol = exam.teacher.split(" [").last().removeSuffix("]")
                    group = exam.subject.split("|").last()
                    if (group.contains(" ")) group = ""
                }
            }
        }.flatten()
    }.flatten().filter {
        it.date.toLocalDate() >= startDate && it.date.toLocalDate() <= end
    }.sortedBy { it.date }.toList()
}
