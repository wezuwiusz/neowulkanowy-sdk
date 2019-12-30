package io.github.wulkanowy.sdk.mapper

import io.github.wulkanowy.sdk.scrapper.toLocalDate
import io.github.wulkanowy.sdk.mobile.dictionaries.Dictionaries
import io.github.wulkanowy.sdk.pojo.Absent
import io.github.wulkanowy.sdk.pojo.Attendance
import io.github.wulkanowy.sdk.pojo.AttendanceSummary
import io.github.wulkanowy.sdk.scrapper.attendance.Absent as ScrapperAbsent
import io.github.wulkanowy.sdk.toLocalDate
import io.github.wulkanowy.sdk.scrapper.attendance.Attendance as ScrapperAttendance
import io.github.wulkanowy.sdk.scrapper.attendance.AttendanceSummary as ScrapperAttendanceSummary
import io.github.wulkanowy.sdk.mobile.attendance.Attendance as ApiAttendance

fun List<ApiAttendance>.mapAttendance(dictionaries: Dictionaries): List<Attendance> {
    return map {
        val category = dictionaries.attendanceCategories.singleOrNull { cat -> cat.id == it.categoryId }
        Attendance(
            number = it.number,
            name = category?.name.orEmpty().capitalize(),
            subject = it.subjectName,
            date = it.date.toLocalDate(),
            timeId = -1,
            absence = category?.absence ?: false,
            categoryId = it.categoryId,
            presence = category?.presence ?: false,
            lateness = category?.lateness ?: false,
            exemption = category?.exemption ?: false,
            excused = category?.excused ?: false,
            excusable = false, //
            deleted = category?.deleted ?: false,
            excuseStatus = null
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
            timeId = it.timeId,
            absence = it.absence,
            categoryId = it.categoryId,
            deleted = it.deleted,
            excusable = it.excusable,
            excused = it.excused,
            exemption = it.exemption,
            lateness = it.lateness,
            presence = it.presence,
            excuseStatus = it.excuseStatus
        )
    }
}

fun List<ScrapperAttendanceSummary>.mapAttendanceSummary(): List<AttendanceSummary> {
    return map {
        AttendanceSummary(
            month = it.month,
            presence = it.presence,
            absence = it.absence,
            absenceExcused = it.absenceExcused,
            absenceForSchoolReasons = it.absenceForSchoolReasons,
            lateness = it.lateness,
            latenessExcused = it.latenessExcused,
            exemption = it.exemption
        )
    }
}

fun List<Absent>.mapToScrapperAbsent(): List<ScrapperAbsent> {
    return map {
        ScrapperAbsent(
            date = it.date,
            timeId = it.timeId
        )
    }
}
