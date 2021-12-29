package io.github.wulkanowy.sdk.mapper

import io.github.wulkanowy.sdk.mobile.dictionaries.Dictionaries
import io.github.wulkanowy.sdk.pojo.CompletedLesson
import io.github.wulkanowy.sdk.pojo.Timetable
import io.github.wulkanowy.sdk.pojo.TimetableAdditional
import io.github.wulkanowy.sdk.pojo.TimetableDayHeader
import io.github.wulkanowy.sdk.pojo.TimetableFull
import io.github.wulkanowy.sdk.scrapper.toLocalDate
import io.github.wulkanowy.sdk.toLocalDate
import io.github.wulkanowy.sdk.toLocalDateTime
import java.time.ZoneId
import io.github.wulkanowy.sdk.mobile.timetable.Lesson as ApiTimetable
import io.github.wulkanowy.sdk.scrapper.timetable.CompletedLesson as ScrapperCompletedLesson
import io.github.wulkanowy.sdk.scrapper.timetable.Timetable as ScrapperTimetable
import io.github.wulkanowy.sdk.scrapper.timetable.TimetableAdditional as ScrapperTimetableAdditional
import io.github.wulkanowy.sdk.scrapper.timetable.TimetableDayHeader as ScrapperTimetableDayHeader
import io.github.wulkanowy.sdk.scrapper.timetable.TimetableFull as ScrapperTimetableFull

fun List<ApiTimetable>.mapTimetableFull(dictionaries: Dictionaries, zoneId: ZoneId) = TimetableFull(
    headers = emptyList(),
    lessons = mapTimetable(dictionaries, zoneId),
    additional = emptyList()
)

fun List<ApiTimetable>.mapTimetable(dictionaries: Dictionaries, zoneId: ZoneId) = map {
    val teacher = dictionaries.employees.singleOrNull { employee -> employee.id == it.employeeId }
    val teacherOld = dictionaries.employees.singleOrNull { employee -> employee.id == it.employeeOldId }
    val time = dictionaries.lessonTimes.single { time -> time.id == it.lessonTimeId }
    val startDateTime = "${it.dayText} ${time.startText}".toLocalDateTime("yyyy-MM-dd HH:mm")
    val endDateTime = "${it.dayText} ${time.endText}".toLocalDateTime("yyyy-MM-dd HH:mm")
    Timetable(
        canceled = it.overriddenName,
        changes = it.boldName || (!it.annotationAboutChange.isNullOrBlank() && !it.overriddenName),
        date = it.day.toLocalDate(),
        start = startDateTime,
        end = endDateTime,
        startZoned = startDateTime.atZone(zoneId),
        endZoned = endDateTime.atZone(zoneId),
        group = it.divisionShort.orEmpty(),
        info = it.annotationAboutChange?.substringAfter("(")?.substringBefore(")").orEmpty(),
        number = it.lessonNumber,
        room = it.room.orEmpty(),
        roomOld = "",
        subject = it.subjectName,
        subjectOld = "",
        studentPlan = it.studentPlan,
        teacher = teacher?.run { "$name $surname" }.orEmpty(),
        teacherOld = teacherOld?.run { "$name $surname" }.orEmpty()
    )
}.groupBy { Triple(it.date, it.number, it.studentPlan) }.map { (_, lessons) ->
    if (lessons.size > 1 && lessons.any { !it.canceled } && lessons.any { it.canceled }) {
        val canceled = lessons.first { it.canceled }
        val lesson = lessons.first { !it.canceled }.copy(
            subjectOld = canceled.subject,
            teacherOld = canceled.teacher,
            roomOld = canceled.room
        )
        listOf(lesson)
    } else lessons
}.flatten()

fun ScrapperTimetableFull.mapTimetableFull(zoneId: ZoneId) = TimetableFull(
    headers = headers.mapTimetableDayHeaders(),
    lessons = lessons.mapTimetable(zoneId),
    additional = additional.mapTimetableAdditional(zoneId)
)

fun List<ScrapperTimetable>.mapTimetable(zoneId: ZoneId) = map {
    Timetable(
        canceled = it.canceled,
        changes = it.changes,
        date = it.date,
        start = it.start,
        end = it.end,
        startZoned = it.start.atZone(zoneId),
        endZoned = it.end.atZone(zoneId),
        group = it.group,
        info = it.info,
        number = it.number,
        room = it.room,
        roomOld = it.roomOld,
        subject = it.subject,
        subjectOld = it.subjectOld,
        studentPlan = true,
        teacher = it.teacher,
        teacherOld = it.teacherOld
    )
}

fun List<ScrapperTimetableDayHeader>.mapTimetableDayHeaders() = map {
    TimetableDayHeader(
        date = it.date,
        content = it.content
    )
}

fun List<ScrapperTimetableAdditional>.mapTimetableAdditional(zoneId: ZoneId) = map {
    TimetableAdditional(
        subject = it.subject,
        date = it.date,
        start = it.start,
        end = it.end,
        startZoned = it.start.atZone(zoneId),
        endZoned = it.end.atZone(zoneId),
    )
}

fun List<ScrapperCompletedLesson>.mapCompletedLessons() = map {
    CompletedLesson(
        date = it.date.toLocalDate(),
        number = it.number,
        subject = it.subject,
        topic = it.topic,
        teacher = it.teacher,
        teacherSymbol = it.teacherSymbol,
        substitution = it.substitution,
        absence = it.absence,
        resources = it.resources
    )
}
