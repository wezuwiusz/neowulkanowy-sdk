package io.github.wulkanowy.sdk.mapper

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
import io.github.wulkanowy.sdk.scrapper.attendance.Absent as ScrapperAbsent
import io.github.wulkanowy.sdk.scrapper.attendance.Attendance as ScrapperAttendance
import io.github.wulkanowy.sdk.scrapper.attendance.AttendanceSummary as ScrapperAttendanceSummary

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
        presence = it.category == PRESENCE || it.category == ABSENCE_FOR_SCHOOL_REASONS,
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
        exemption = it.exemption,
    )
}

fun List<Absent>.mapToScrapperAbsent() = map {
    ScrapperAbsent(
        date = it.date,
        timeId = it.timeId,
    )
}
