package io.github.wulkanowy.sdk.mapper

import io.github.wulkanowy.sdk.pojo.Absent
import io.github.wulkanowy.sdk.pojo.Attendance
import io.github.wulkanowy.sdk.pojo.AttendanceSummary
import io.github.wulkanowy.sdk.scrapper.attendance.AttendanceCategory
import io.github.wulkanowy.sdk.scrapper.attendance.AttendanceCategory.ABSENCE_EXCUSED
import io.github.wulkanowy.sdk.scrapper.attendance.AttendanceCategory.ABSENCE_FOR_SCHOOL_REASONS
import io.github.wulkanowy.sdk.scrapper.attendance.AttendanceCategory.ABSENCE_UNEXCUSED
import io.github.wulkanowy.sdk.scrapper.attendance.AttendanceCategory.EXCUSED_LATENESS
import io.github.wulkanowy.sdk.scrapper.attendance.AttendanceCategory.EXEMPTION
import io.github.wulkanowy.sdk.scrapper.attendance.AttendanceCategory.PRESENCE
import io.github.wulkanowy.sdk.scrapper.attendance.AttendanceCategory.UNEXCUSED_LATENESS
import io.github.wulkanowy.sdk.hebe.models.AttendanceSummary as HebeAttendanceSummary
import io.github.wulkanowy.sdk.hebe.models.CompletedLesson as HebeCompletedLesson
import io.github.wulkanowy.sdk.scrapper.attendance.Absent as ScrapperAbsent
import io.github.wulkanowy.sdk.scrapper.attendance.Attendance as ScrapperAttendance
import io.github.wulkanowy.sdk.scrapper.attendance.AttendanceSummary as ScrapperAttendanceSummary

@JvmName("mapScrapperAttendance")
internal fun List<ScrapperAttendance>.mapAttendance() = map {
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

@JvmName("mapScrapperAttendanceSummary")
internal fun List<ScrapperAttendanceSummary>.mapAttendanceSummary() = map {
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

@JvmName("mapScrapperAbsence")
internal fun List<Absent>.mapToScrapperAbsent() = map {
    ScrapperAbsent(
        date = it.date,
        timeId = it.timeId,
    )
}

@JvmName("mapHebeAttendanceSummary")
internal fun List<HebeAttendanceSummary>.mapAttendanceSummary() = map {
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

@JvmName("mapHebeAttendance")
internal fun List<HebeCompletedLesson>.mapAttendance() = map {
    val category = AttendanceCategory.getCategoryById(it.presenceType?.categoryId ?: 0)

    Attendance(
        number = it.timeSlot.position,
        name = category.toString(),
        subject = it.subject?.name ?: "",
        date = it.day.date,
        timeId = it.timeSlot.id,
        categoryId = it.presenceType?.categoryId ?: 0,
        deleted = it.presenceType?.removed ?: false,
        excuseStatus = null,
        excusable = false,
        absence = category == ABSENCE_UNEXCUSED || category == ABSENCE_EXCUSED,
        excused = category == ABSENCE_EXCUSED || category == EXCUSED_LATENESS,
        exemption = category == EXEMPTION,
        lateness = category == EXCUSED_LATENESS || category == UNEXCUSED_LATENESS,
        presence = category == PRESENCE || category == ABSENCE_FOR_SCHOOL_REASONS,
    )
}
