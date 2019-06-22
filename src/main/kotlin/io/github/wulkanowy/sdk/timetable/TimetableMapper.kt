package io.github.wulkanowy.sdk.timetable

import io.github.wulkanowy.api.toLocalDate
import io.github.wulkanowy.sdk.dictionaries.Dictionaries
import io.github.wulkanowy.sdk.pojo.Timetable
import io.github.wulkanowy.sdk.toLocalDate
import io.github.wulkanowy.sdk.toLocalDateTime
import io.github.wulkanowy.api.timetable.Timetable as ScrapperTimetable
import io.github.wulkanowy.sdk.timetable.Lesson as ApiTimetable

fun List<ApiTimetable>.mapTimetable(dictionaries: Dictionaries): List<Timetable> {
    return map {
        val teacher = dictionaries.employees.singleOrNull { employee -> employee.id == it.employeeId }
        val teacherOld = dictionaries.employees.singleOrNull { employee -> employee.id == it.employeeOldId }
        val time = dictionaries.lessonTimes.single { time -> time.id == it.lessonTimeId }
        Timetable(
            canceled = it.overriddenName,
            changes = it.boldName || it.annotationAboutChange.isNotBlank(),
            date = it.day.toLocalDate(),
            end = time.end.toLocalDateTime(),
            group = it.divisionShort.orEmpty(),
            info = it.annotationAboutChange.substringAfter("(").substringBefore(")"),
            number = it.lessonNumber,
            room = it.room.orEmpty(),
            roomOld = "",
            start = time.start.toLocalDateTime(),
            subject = it.subjectName,
            subjectOld = "",
            studentPlan = it.studentPlan,
            teacher = teacher?.run { "$name $surname" }.orEmpty(),
            teacherOld = teacherOld?.run { "$name $surname" }.orEmpty()
        )
    }
}

fun List<ScrapperTimetable>.mapTimetable(): List<Timetable> {
    return map {
        Timetable(
            canceled = it.canceled,
            changes = it.changes,
            date = it.date.toLocalDate(),
            end = it.end.toLocalDateTime(),
            group = it.group,
            info = it.info,
            number = it.number,
            room = it.room,
            roomOld = it.roomOld,
            start = it.start.toLocalDateTime(),
            subject = it.subject,
            subjectOld = it.subjectOld,
            studentPlan = true,
            teacher = it.teacher,
            teacherOld = it.teacherOld
        )
    }
}
