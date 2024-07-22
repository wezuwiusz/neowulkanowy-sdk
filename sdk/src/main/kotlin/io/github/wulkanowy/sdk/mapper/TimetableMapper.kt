package io.github.wulkanowy.sdk.mapper

import io.github.wulkanowy.sdk.pojo.CompletedLesson
import io.github.wulkanowy.sdk.pojo.Lesson
import io.github.wulkanowy.sdk.pojo.LessonAdditional
import io.github.wulkanowy.sdk.pojo.Timetable
import io.github.wulkanowy.sdk.pojo.TimetableDayHeader
import io.github.wulkanowy.sdk.scrapper.attendance.AttendanceCategory
import java.time.LocalDateTime
import java.time.ZoneId
import io.github.wulkanowy.sdk.hebe.models.CompletedLesson as HebeCompletedLesson
import io.github.wulkanowy.sdk.hebe.models.Lesson as HebeLesson
import io.github.wulkanowy.sdk.hebe.models.TimetableChange as HebeTimetableChange
import io.github.wulkanowy.sdk.hebe.models.TimetableFull as HebeFullTimetable
import io.github.wulkanowy.sdk.hebe.models.TimetableHeader as HebeTimetableHeader
import io.github.wulkanowy.sdk.scrapper.timetable.CompletedLesson as ScrapperCompletedLesson
import io.github.wulkanowy.sdk.scrapper.timetable.Lesson as ScrapperTimetable
import io.github.wulkanowy.sdk.scrapper.timetable.LessonAdditional as ScrapperTimetableAdditional
import io.github.wulkanowy.sdk.scrapper.timetable.Timetable as ScrapperTimetableFull
import io.github.wulkanowy.sdk.scrapper.timetable.TimetableDayHeader as ScrapperTimetableDayHeader

@JvmName("mapScrapperFullTimetable")
internal fun ScrapperTimetableFull.mapTimetableFull(zoneId: ZoneId) = Timetable(
    headers = headers.mapTimetableDayHeaders(),
    lessons = lessons.mapTimetable(zoneId),
    additional = additional.mapTimetableAdditional(zoneId),
)

@JvmName("mapScrapperTimetable")
internal fun List<ScrapperTimetable>.mapTimetable(zoneId: ZoneId) = map {
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

@JvmName("mapScrapperTimetableHeader")
internal fun List<ScrapperTimetableDayHeader>.mapTimetableDayHeaders() = map {
    TimetableDayHeader(
        date = it.date,
        content = it.content,
    )
}

@JvmName("mapScrapperTimetableAdditional")
internal fun List<ScrapperTimetableAdditional>.mapTimetableAdditional(zoneId: ZoneId) = map {
    LessonAdditional(
        subject = it.subject,
        date = it.date,
        start = it.start.atZone(zoneId),
        end = it.end.atZone(zoneId),
    )
}

@JvmName("mapScrapperCompletedLesson")
internal fun List<ScrapperCompletedLesson>.mapCompletedLessons() = map {
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

@JvmName("mapHebeFullTimetable")
internal fun HebeFullTimetable.mapTimetableFull(zoneId: ZoneId) = Timetable(
    headers = headers.mapTimetableDayHeaders(),
    lessons = lessons.mapTimetable(zoneId, changes),
    additional = emptyList(),
)

@JvmName("mapHebeTimetable")
internal fun List<HebeLesson>.mapTimetable(zoneId: ZoneId, changes: List<HebeTimetableChange>) = map {
    val timeSlotStart = it.timeSlot.start.split(":")
    val timeSlotEnd = it.timeSlot.end.split(":")
    val change = changes.find { change -> it.change?.id == change.id }

    Lesson(
        canceled = change?.change?.type == 1 || change?.change?.type == 4,
        changes = change != null && change.change.type != 1 && change.change.type != 4,
        date = it.date.date,
        start = LocalDateTime
            .of(
                it.date.date.year,
                it.date.date.monthValue,
                it.date.date.dayOfMonth,
                timeSlotStart[0].toInt(),
                timeSlotStart[1].toInt(),
            ).atZone(zoneId),
        end = LocalDateTime
            .of(
                it.date.date.year,
                it.date.date.monthValue,
                it.date.date.dayOfMonth,
                timeSlotEnd[0].toInt(),
                timeSlotEnd[1].toInt(),
            ).atZone(zoneId),
        group = it.distribution?.name ?: "",
        info = if (change?.change?.type == 4) change.reason ?: "" else change?.teacherAbsenceEffectName ?: "",
        number = it.timeSlot.position,
        room = change?.room?.code ?: it.room?.code ?: "",
        roomOld = if (change?.room != null) it.room?.code ?: "" else "",
        subject = change?.subject?.name ?: it.subject?.name ?: "",
        subjectOld = if (change?.subject != null) it.subject?.name ?: "" else "",
        studentPlan = it.visible,
        teacher = change?.teacherPrimary?.displayName ?: it.teacherPrimary?.displayName ?: "",
        teacherOld = if (change?.teacherPrimary != null) it.teacherPrimary?.displayName ?: "" else "",
    )
}

@JvmName("mapHebeTimetableHeader")
internal fun List<HebeTimetableHeader>.mapTimetableDayHeaders() = map {
    TimetableDayHeader(
        date = it.date,
        content = it.content,
    )
}

@JvmName("mapHebeCompletedLesson")
internal fun List<HebeCompletedLesson>.mapCompletedLessons() = map {
    val absence = AttendanceCategory.getCategoryById(it.presenceType?.categoryId ?: 0).title

    CompletedLesson(
        date = it.day.date,
        number = it.timeSlot.position,
        subject = it.subject?.name ?: "",
        topic = it.topic.orEmpty(),
        teacher = it.primaryTeacher?.displayName ?: "",
        teacherSymbol = (
            it.primaryTeacher
                ?.name
                ?.first()
                .toString() + it.primaryTeacher
                ?.surname
                ?.first()
                .toString()
        ),
        substitution = "",
        absence = if (absence == "Obecność") "" else absence,
        resources = "",
    )
}
