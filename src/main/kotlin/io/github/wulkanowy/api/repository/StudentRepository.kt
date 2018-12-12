package io.github.wulkanowy.api.repository

import io.github.wulkanowy.api.exams.Exam
import io.github.wulkanowy.api.exams.ExamRequest
import io.github.wulkanowy.api.getGradeShortValue
import io.github.wulkanowy.api.grades.*
import io.github.wulkanowy.api.homework.Homework
import io.github.wulkanowy.api.mobile.Device
import io.github.wulkanowy.api.service.StudentService
import io.github.wulkanowy.api.timetable.Timetable
import io.github.wulkanowy.api.timetable.TimetableParser
import io.github.wulkanowy.api.timetable.TimetableRequest
import io.github.wulkanowy.api.timetable.TimetableResponse
import io.github.wulkanowy.api.toDate
import io.github.wulkanowy.api.toFormat
import io.github.wulkanowy.api.toLocalDate
import io.reactivex.Single
import org.jsoup.Jsoup
import org.threeten.bp.LocalDate

class StudentRepository(private val api: StudentService) {

    fun getExams(startDate: LocalDate, endDate: LocalDate? = null): Single<List<Exam>> {
        val end = endDate ?: startDate.plusDays(4)
        return api.getExams(ExamRequest(startDate.toDate(), startDate.year)).map { res ->
            res.data?.map { weeks ->
                weeks.weeks.map { day ->
                    day.exams.map { exam ->
                        exam.apply {
                            group = subject.split("|").last()
                            if (group.contains(" ")) group = ""
                            date = day.date
                            type = if ("2" == type) "Sprawdzian" else "Kartkówka"
                            teacherSymbol = teacher.split(" [").last().removeSuffix("]")
                            teacher = teacher.split(" [").first()
                        }
                    }
                }.flatten()
            }?.flatten()?.filter {
                it.date.toLocalDate() >= startDate && it.date.toLocalDate() <= end
            }?.sortedBy { it.date }?.toList()
        }
    }

    fun getGrades(semesterId: Int?): Single<List<Grade>> {
        return api.getGrades(GradeRequest(semesterId)).map { res ->
            res.data?.gradesWithSubjects?.map { subject ->
                subject.grades.map {
                    val values = getGradeValueWithModifier(it.entry)
                    it.apply {
                        this.subject = subject.name
                        comment = entry.substringAfter(" (").removeSuffix(")")
                        entry = entry.substringBefore(" (")
                        if (comment == entry) comment = ""
                        value = values.first
                        date = privateDate
                        modifier = values.second
                        weight = "$weightValue,00"
                        weightValue = if (isGradeValid(entry)) weightValue else 0
                        color = if ("0" == color) "000000" else color.toInt().toString(16).toUpperCase()
                    }
                }.sortedByDescending { it.date }
            }?.flatten()
        }
    }

    fun getGradesSummary(semesterId: Int?): Single<List<GradeSummary>> {
        return api.getGrades(GradeRequest(semesterId)).map { res ->
            res.data?.gradesWithSubjects?.map { subject ->
                GradeSummary().apply {
                    name = subject.name
                    predicted = getGradeShortValue(subject.proposed)
                    final = getGradeShortValue(subject.annual)
                }
            }?.sortedBy { it.name }?.toList()
        }
    }

    fun getHomework(startDate: LocalDate, endDate: LocalDate? = null): Single<List<Homework>> {
        val end = endDate ?: startDate
        return api.getHomework(ExamRequest(startDate.toDate(), startDate.year)).map { res ->
            res.data?.map { day ->
                day.items.map {
                    val teacherAndDate = it.teacher.split(", ")
                    it.apply {
                        date = day.date
                        entryDate = teacherAndDate.last().toDate("dd.MM.yyyy")
                        teacher = teacherAndDate.first().split(" [").first()
                        teacherSymbol = teacherAndDate.first().split(" [").last().removeSuffix("]")
                    }
                }
            }?.flatten()?.filter {
                it.date.toLocalDate() in startDate..end
            }?.sortedWith(compareBy({ it.date }, { it.subject }))?.toList()
        }
    }

    fun getTimetable(startDate: LocalDate, endDate: LocalDate? = null): Single<List<Timetable>> {
        return api.getTimetable(TimetableRequest(startDate.toFormat("yyyy-MM-dd'T00:00:00'"))).map { res ->
            res.data?.rows2api?.flatMap { lessons ->
                lessons.drop(1).mapIndexed { i, it ->
                    val times = lessons[0].split("<br />")
                    TimetableResponse.TimetableRow.TimetableCell().apply {
                        date = res.data.header.drop(1)[i].date.split("<br />")[1].toDate("dd.MM.yyyy")
                        start = "${date.toLocalDate().toFormat("yyyy-MM-dd")} ${times[1]}".toDate("yyyy-MM-dd HH:mm")
                        end = "${date.toLocalDate().toFormat("yyyy-MM-dd")} ${times[2]}".toDate("yyyy-MM-dd HH:mm")
                        number = times[0].toInt()
                        td = Jsoup.parse(it)
                    }
                }.mapNotNull { TimetableParser().getTimetable(it) }
            }?.asSequence()?.filter {
                it.date.toLocalDate() >= startDate && it.date.toLocalDate() <= endDate ?: startDate.plusDays(4)
            }?.sortedWith(compareBy({ it.date }, { it.number }))?.toList()
        }
    }

    fun getRegisteredDevices(): Single<List<Device>> {
        return api.getRegisteredDevices().map { it.data }
    }
}
