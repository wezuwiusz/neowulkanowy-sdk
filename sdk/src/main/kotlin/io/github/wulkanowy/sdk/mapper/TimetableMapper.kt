package io.github.wulkanowy.sdk.mapper

import io.github.wulkanowy.sdk.scrapper.timetable.CompletedLesson as ScrapperCompletedLesson
import io.github.wulkanowy.sdk.pojo.CompletedLesson
import io.github.wulkanowy.sdk.scrapper.toLocalDate
import io.github.wulkanowy.sdk.mobile.dictionaries.Dictionaries
import io.github.wulkanowy.sdk.pojo.Timetable
import io.github.wulkanowy.sdk.toLocalDate
import io.github.wulkanowy.sdk.toLocalDateTime
import io.github.wulkanowy.sdk.scrapper.timetable.Timetable as ScrapperTimetable
import io.github.wulkanowy.sdk.mobile.timetable.Lesson as ApiTimetable

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

fun List<ScrapperCompletedLesson>.mapCompletedLessons(): List<CompletedLesson> {
    return map {
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
}
