package io.github.wulkanowy.sdk.mapper

import io.github.wulkanowy.sdk.mobile.dictionaries.Dictionaries
import io.github.wulkanowy.sdk.pojo.Absent
import io.github.wulkanowy.sdk.pojo.Attendance
import io.github.wulkanowy.sdk.pojo.AttendanceSummary
import io.github.wulkanowy.sdk.scrapper.attendance.AttendanceCategory.ABSENCE_EXCUSED
import io.github.wulkanowy.sdk.scrapper.attendance.AttendanceCategory.ABSENCE_FOR_SCHOOL_REASONS
import io.github.wulkanowy.sdk.scrapper.attendance.AttendanceCategory.ABSENCE_UNEXCUSED
import io.github.wulkanowy.sdk.scrapper.attendance.AttendanceCategory.EXCUSED_LATENESS
import io.github.wulkanowy.sdk.scrapper.attendance.AttendanceCategory.EXEMPTION
import io.github.wulkanowy.sdk.scrapper.attendance.AttendanceCategory.PRESENCE
import io.github.wulkanowy.sdk.scrapper.attendance.AttendanceCategory.UNEXCUSED_LATENESS
import io.github.wulkanowy.sdk.scrapper.capitalise
import io.github.wulkanowy.sdk.scrapper.toLocalDate
import io.github.wulkanowy.sdk.toLocalDate
import io.github.wulkanowy.sdk.mobile.attendance.Attendance as ApiAttendance
import io.github.wulkanowy.sdk.scrapper.attendance.Absent as ScrapperAbsent
import io.github.wulkanowy.sdk.scrapper.attendance.Attendance as ScrapperAttendance
import io.github.wulkanowy.sdk.scrapper.attendance.AttendanceSummary as ScrapperAttendanceSummary

fun List<ApiAttendance>.mapAttendance(dictionaries: Dictionaries) = map {
    val category = dictionaries.attendanceCategories.singleOrNull { cat -> cat.id == it.categoryId }
    Attendance(
        number = it.number,
        name = category?.name?.capitalise() ?: "Nieznany",
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

fun List<ScrapperAttendance>.mapAttendance() = map {
    Attendance(
        number = it.number,
        name = it.category.name,
        subject = it.subject.orEmpty(),
        date = it.date.toLocalDate(),
        timeId = it.timeId,
        categoryId = it.categoryId,
        deleted = false,
        excuseStatus = it.excuseStatus,
        excusable = it.excusable,
        absence = it.category == ABSENCE_UNEXCUSED || it.category == ABSENCE_EXCUSED,
        excused = it.category == ABSENCE_EXCUSED || it.category == EXCUSED_LATENESS,
        exemption = it.category == EXEMPTION,
        lateness = it.category == EXCUSED_LATENESS || it.category == UNEXCUSED_LATENESS,
        presence = it.category == PRESENCE || it.category == ABSENCE_FOR_SCHOOL_REASONS
    )
}

fun List<ScrapperAttendanceSummary>.mapAttendanceSummary() = map {
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

fun List<Absent>.mapToScrapperAbsent() = map {
    ScrapperAbsent(
        date = it.date,
        timeId = it.timeId
    )
}
