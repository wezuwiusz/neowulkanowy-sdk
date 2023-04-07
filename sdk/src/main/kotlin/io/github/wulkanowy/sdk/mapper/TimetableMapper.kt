package io.github.wulkanowy.sdk.mapper

import io.github.wulkanowy.sdk.pojo.CompletedLesson
import io.github.wulkanowy.sdk.pojo.Lesson
import io.github.wulkanowy.sdk.pojo.LessonAdditional
import io.github.wulkanowy.sdk.pojo.Timetable
import io.github.wulkanowy.sdk.pojo.TimetableDayHeader
import java.time.ZoneId
import io.github.wulkanowy.sdk.scrapper.timetable.CompletedLesson as ScrapperCompletedLesson
import io.github.wulkanowy.sdk.scrapper.timetable.Lesson as ScrapperTimetable
import io.github.wulkanowy.sdk.scrapper.timetable.LessonAdditional as ScrapperTimetableAdditional
import io.github.wulkanowy.sdk.scrapper.timetable.Timetable as ScrapperTimetableFull
import io.github.wulkanowy.sdk.scrapper.timetable.TimetableDayHeader as ScrapperTimetableDayHeader

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
