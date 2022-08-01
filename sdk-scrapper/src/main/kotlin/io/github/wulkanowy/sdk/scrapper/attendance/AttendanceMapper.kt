package io.github.wulkanowy.sdk.scrapper.attendance

import io.github.wulkanowy.sdk.scrapper.attendance.AttendanceCategory.ABSENCE_UNEXCUSED
import io.github.wulkanowy.sdk.scrapper.attendance.AttendanceCategory.UNEXCUSED_LATENESS
import io.github.wulkanowy.sdk.scrapper.timetable.CacheResponse.Time
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.LocalDate
import java.time.Month

fun AttendanceResponse.mapAttendanceList(start: LocalDate, end: LocalDate?, times: List<Time>): List<Attendance> {
    val endDate = end ?: start.plusDays(4)
    return lessons.map {
        val sentExcuse = sentExcuses.firstOrNull { excuse -> excuse.date == it.date && excuse.timeId == it.timeId }
        it.apply {
            number = times.single { time -> time.id == it.timeId }.number
            category = AttendanceCategory.getCategoryById(categoryId)
            excusable = excuseActive && (category == ABSENCE_UNEXCUSED || category == UNEXCUSED_LATENESS) && sentExcuse == null
            if (sentExcuse != null) excuseStatus = SentExcuse.Status.getByValue(sentExcuse.status)
        }
    }.filter {
        it.date.toLocalDate() >= start && it.date.toLocalDate() <= endDate
    }.sortedWith(compareBy({ it.date }, { it.number }))
}

fun AttendanceSummaryResponse.mapAttendanceSummaryList(): List<AttendanceSummary> {
    val jsonObject = Json {
        isLenient = true
    }
    val stats = items.map {
        val json = jsonObject.encodeToString(it)
        jsonObject.decodeFromString<Map<String, String?>>(json)
    }

    val getMonthValue = fun(type: Int, month: Int): Int {
        return stats[type][stats[0].keys.toTypedArray()[month + 1]]?.toInt() ?: 0
    }

    return (1..12).map {
        AttendanceSummary(
            month = Month.of(if (it < 5) 8 + it else it - 4),
            presence = getMonthValue(0, it), absence = getMonthValue(1, it), absenceExcused = getMonthValue(2, it), absenceForSchoolReasons = getMonthValue(3, it),
            lateness = getMonthValue(4, it), latenessExcused = getMonthValue(5, it), exemption = getMonthValue(6, it)
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
