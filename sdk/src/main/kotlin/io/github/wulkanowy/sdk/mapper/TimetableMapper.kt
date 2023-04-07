package io.github.wulkanowy.sdk.mapper

import io.github.wulkanowy.sdk.mobile.dictionaries.Dictionaries
import io.github.wulkanowy.sdk.pojo.CompletedLesson
import io.github.wulkanowy.sdk.pojo.Lesson
import io.github.wulkanowy.sdk.pojo.LessonAdditional
import io.github.wulkanowy.sdk.pojo.TimetableDayHeader
import io.github.wulkanowy.sdk.pojo.Timetable
import io.github.wulkanowy.sdk.toLocalDate
import io.github.wulkanowy.sdk.toLocalDateTime
import java.time.ZoneId
import io.github.wulkanowy.sdk.mobile.timetable.Lesson as ApiTimetable
import io.github.wulkanowy.sdk.scrapper.timetable.CompletedLesson as ScrapperCompletedLesson
import io.github.wulkanowy.sdk.scrapper.timetable.Lesson as ScrapperTimetable
import io.github.wulkanowy.sdk.scrapper.timetable.LessonAdditional as ScrapperTimetableAdditional
import io.github.wulkanowy.sdk.scrapper.timetable.TimetableDayHeader as ScrapperTimetableDayHeader
import io.github.wulkanowy.sdk.scrapper.timetable.Timetable as ScrapperTimetableFull

fun List<ApiTimetable>.mapTimetableFull(dictionaries: Dictionaries, zoneId: ZoneId) = Timetable(
    headers = emptyList(),
    lessons = mapTimetable(dictionaries, zoneId),
    additional = emptyList(),
)

fun List<ApiTimetable>.mapTimetable(dictionaries: Dictionaries, zoneId: ZoneId) = map {
    val teacher = dictionaries.employees.singleOrNull { employee -> employee.id == it.employeeId }
    val teacherOld = dictionaries.employees.singleOrNull { employee -> employee.id == it.employeeOldId }
    val time = dictionaries.lessonTimes.single { time -> time.id == it.lessonTimeId }
    val startDateTime = "${it.dayText} ${time.startText}".toLocalDateTime("yyyy-MM-dd HH:mm")
    val endDateTime = "${it.dayText} ${time.endText}".toLocalDateTime("yyyy-MM-dd HH:mm")
    Lesson(
        canceled = it.overriddenName,
        changes = it.boldName || (!it.annotationAboutChange.isNullOrBlank() && !it.overriddenName),
        date = it.day.toLocalDate(),
        start = startDateTime.atZone(zoneId),
        end = endDateTime.atZone(zoneId),
        group = it.divisionShort.orEmpty(),
        info = it.annotationAboutChange?.substringAfter("(")?.substringBefore(")").orEmpty(),
        number = it.lessonNumber,
        room = it.room.orEmpty(),
        roomOld = "",
        subject = it.subjectName,
        subjectOld = "",
        studentPlan = it.studentPlan,
        teacher = teacher?.run { "$name $surname" }.orEmpty(),
        teacherOld = teacherOld?.run { "$name $surname" }.orEmpty(),
    )
}.groupBy { Triple(it.date, it.number, it.studentPlan) }.map { (_, lessons) ->
    if (lessons.size > 1 && lessons.any { !it.canceled } && lessons.any { it.canceled }) {
        val canceled = lessons.first { it.canceled }
        val lesson = lessons.first { !it.canceled }.copy(
            subjectOld = canceled.subject,
            teacherOld = canceled.teacher,
            roomOld = canceled.room,
        )
        listOf(lesson)
    } else lessons
}.flatten()

fun ScrapperTimetableFull.mapTimetableFull(zoneId: ZoneId) = Timetable(
    headers = headers.mapTimetableDayHeaders(),
    lessons = lessons.mapTimetable(zoneId),
    additional = additional.mapTimetableAdditional(zoneId),
)

fun List<ScrapperTimetable>.mapTimetable(zoneId: ZoneId) = map {
    Lesson(
        canceled = it.canceled,
        changes = it.changes,
        date = it.date,
        start = it.start.atZone(zoneId),
        end = it.end.atZone(zoneId),
        group = it.group,
        info = it.info,
        number = it.number,
        room = it.room,
        roomOld = it.roomOld,
        subject = it.subject,
        subjectOld = it.subjectOld,
        studentPlan = true,
        teacher = it.teacher,
        teacherOld = it.teacherOld,
    )
}

fun List<ScrapperTimetableDayHeader>.mapTimetableDayHeaders() = map {
    TimetableDayHeader(
        date = it.date,
        content = it.content,
    )
}

fun List<ScrapperTimetableAdditional>.mapTimetableAdditional(zoneId: ZoneId) = map {
    LessonAdditional(
        subject = it.subject,
        date = it.date,
        start = it.start.atZone(zoneId),
        end = it.end.atZone(zoneId),
    )
}

fun List<ScrapperCompletedLesson>.mapCompletedLessons() = map {
    CompletedLesson(
        date = it.date.toLocalDate(),
        number = it.number,
        subject = it.subject.orEmpty(),
        topic = it.topic.orEmpty(),
        teacher = it.teacher.orEmpty(),
        teacherSymbol = it.teacherSymbol.orEmpty(),
        substitution = it.substitution.orEmpty(),
        absence = it.absence.orEmpty(),
        resources = it.resources.orEmpty(),
    )
}
