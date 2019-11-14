package io.github.wulkanowy.api.exams

import io.github.wulkanowy.api.toLocalDate
import org.threeten.bp.LocalDate

fun List<ExamResponse>.mapExamsList(startDate: LocalDate, endDate: LocalDate?): List<Exam> {

    val end = endDate ?: startDate.plusDays(4)
    return asSequence().map { weeks ->
        weeks.weeks.map { day ->
            day.exams.map { exam ->
                exam.apply {
                    group = subject.split("|").last()
                    subject = subject.substringBeforeLast(" ")
                    if (group.contains(" ")) group = ""
                    date = day.date
                    type = when (type) {
                        "1" -> "Sprawdzian"
                        "2" -> "Kartkówka"
                        else -> "Praca klasowa"
                    }
                    teacherSymbol = teacher.split(" [").last().removeSuffix("]")
                    teacher = teacher.split(" [").first()
                }
            }
        }.flatten()
    }.flatten().filter {
        it.date.toLocalDate() >= startDate && it.date.toLocalDate() <= end
    }.sortedBy { it.date }.toList()
}
