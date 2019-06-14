package io.github.wulkanowy.sdk.attendance

import io.github.wulkanowy.api.toLocalDate
import io.github.wulkanowy.sdk.dictionaries.Dictionaries
import io.github.wulkanowy.sdk.pojo.Attendance
import io.github.wulkanowy.sdk.toLocalDate
import io.github.wulkanowy.api.attendance.Attendance as ScrapperAttendance
import io.github.wulkanowy.sdk.attendance.Attendance as ApiAttendance

fun List<ApiAttendance>.mapAttendance(dictionaries: Dictionaries): List<Attendance> {
    return map {
        val category = dictionaries.attendanceCategories.singleOrNull { cat -> cat.id == it.categoryId }
        Attendance(
            number = it.number,
            name = category?.name.orEmpty().capitalize(),
            subject = it.subjectName,
            date = it.date.toLocalDate(),
            absence = category?.absence ?: false,
            categoryId = it.categoryId,
            presence = category?.presence ?: false,
            lateness = category?.lateness ?: false,
            exemption = category?.exemption ?: false,
            excused = category?.excused ?: false,
            excusable = false, //
            deleted = category?.deleted ?: false
        )
    }
}

fun List<ScrapperAttendance>.mapAttendance(): List<Attendance> {
    return map {
        Attendance(
            number = it.number,
            name = it.name,
            subject = it.subject,
            date = it.date.toLocalDate(),
            absence = it.absence,
            categoryId = it.categoryId,
            deleted = it.deleted,
            excusable = it.excusable,
            excused = it.excused,
            exemption = it.exemption,
            lateness = it.lateness,
            presence = it.presence
        )
    }
}
