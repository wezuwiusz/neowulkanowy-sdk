package io.github.wulkanowy.sdk.mapper

import io.github.wulkanowy.sdk.mobile.dictionaries.Dictionaries
import io.github.wulkanowy.sdk.pojo.Absent
import io.github.wulkanowy.sdk.pojo.Attendance
import io.github.wulkanowy.sdk.pojo.AttendanceSummary
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
        name = category?.name?.capitalize() ?: "Nieznany",
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
