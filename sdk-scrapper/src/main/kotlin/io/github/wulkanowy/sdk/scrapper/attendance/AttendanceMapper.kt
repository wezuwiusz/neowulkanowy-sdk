package io.github.wulkanowy.sdk.scrapper.attendance

import com.google.gson.GsonBuilder
import com.google.gson.internal.LinkedTreeMap
import com.google.gson.reflect.TypeToken
import io.github.wulkanowy.sdk.scrapper.attendance.Attendance.Category.ABSENCE_EXCUSED
import io.github.wulkanowy.sdk.scrapper.attendance.Attendance.Category.ABSENCE_FOR_SCHOOL_REASONS
import io.github.wulkanowy.sdk.scrapper.attendance.Attendance.Category.ABSENCE_UNEXCUSED
import io.github.wulkanowy.sdk.scrapper.attendance.Attendance.Category.EXCUSED_LATENESS
import io.github.wulkanowy.sdk.scrapper.attendance.Attendance.Category.EXEMPTION
import io.github.wulkanowy.sdk.scrapper.attendance.Attendance.Category.PRESENCE
import io.github.wulkanowy.sdk.scrapper.attendance.Attendance.Category.UNEXCUSED_LATENESS
import io.github.wulkanowy.sdk.scrapper.attendance.Attendance.Category.UNKNOWN
import io.github.wulkanowy.sdk.scrapper.attendance.Attendance.Category.values
import io.github.wulkanowy.sdk.scrapper.timetable.CacheResponse.Time
import io.github.wulkanowy.sdk.scrapper.toLocalDate
import java.time.LocalDate
import java.time.Month

fun AttendanceResponse.mapAttendanceList(start: LocalDate, end: LocalDate?, times: List<Time>): List<Attendance> {
    val endDate = end ?: start.plusDays(4)
    return lessons.map {
        val sentExcuse = sentExcuses.firstOrNull { excuse -> excuse.date == it.date && excuse.timeId == it.timeId }
        it.apply {
            presence = it.categoryId == PRESENCE.id || it.categoryId == ABSENCE_FOR_SCHOOL_REASONS.id
            absence = it.categoryId == ABSENCE_UNEXCUSED.id || it.categoryId == ABSENCE_EXCUSED.id
            lateness = it.categoryId == EXCUSED_LATENESS.id || it.categoryId == UNEXCUSED_LATENESS.id
            excused = it.categoryId == ABSENCE_EXCUSED.id || it.categoryId == EXCUSED_LATENESS.id
            exemption = it.categoryId == EXEMPTION.id
            excusable = excuseActive && (absence || lateness) && !excused && sentExcuse == null
            name = (values().singleOrNull { category -> category.id == categoryId } ?: UNKNOWN).title
            number = times.single { time -> time.id == it.timeId }.number
            if (sentExcuse != null)
                excuseStatus = SentExcuse.Status.getByValue(sentExcuse.status)
        }
    }.filter {
        it.date.toLocalDate() >= start && it.date.toLocalDate() <= endDate
    }.sortedWith(compareBy({ it.date }, { it.number }))
}

fun AttendanceSummaryResponse.mapAttendanceSummaryList(gson: GsonBuilder): List<AttendanceSummary> {
    val stats = items.map {
        (gson.create().fromJson<LinkedTreeMap<String, String?>>(
            gson.registerTypeAdapter(
                AttendanceSummaryResponse.Summary::class.java,
                AttendanceSummaryItemSerializer()
            ).create().toJson(it), object : TypeToken<LinkedTreeMap<String, String?>>() {}.type
        ))
    }

    val getMonthValue = fun(type: Int, month: Int): Int {
        return stats[type][stats[0].keys.toTypedArray()[month + 1]]?.toInt() ?: 0
    }

    return (1..12).map {
        AttendanceSummary(
            Month.of(if (it < 5) 8 + it else it - 4),
            getMonthValue(0, it), getMonthValue(1, it), getMonthValue(2, it), getMonthValue(3, it),
            getMonthValue(4, it), getMonthValue(5, it), getMonthValue(6, it)
        )
    }.filterNot { summary ->
        summary.absence == 0 &&
            summary.absenceExcused == 0 &&
            summary.absenceForSchoolReasons == 0 &&
            summary.exemption == 0 &&
            summary.lateness == 0 &&
            summary.latenessExcused == 0 &&
            summary.presence == 0
    }
}
