package io.github.wulkanowy.api.attendance

import com.google.gson.GsonBuilder
import com.google.gson.internal.LinkedTreeMap
import com.google.gson.reflect.TypeToken
import io.github.wulkanowy.api.attendance.Attendance.Category.ABSENCE_EXCUSED
import io.github.wulkanowy.api.attendance.Attendance.Category.ABSENCE_FOR_SCHOOL_REASONS
import io.github.wulkanowy.api.attendance.Attendance.Category.ABSENCE_UNEXCUSED
import io.github.wulkanowy.api.attendance.Attendance.Category.EXCUSED_LATENESS
import io.github.wulkanowy.api.attendance.Attendance.Category.EXEMPTION
import io.github.wulkanowy.api.attendance.Attendance.Category.PRESENCE
import io.github.wulkanowy.api.attendance.Attendance.Category.UNEXCUSED_LATENESS
import io.github.wulkanowy.api.attendance.Attendance.Category.UNKNOWN
import io.github.wulkanowy.api.attendance.Attendance.Category.values
import io.github.wulkanowy.api.timetable.CacheResponse.Time
import io.github.wulkanowy.api.toLocalDate
import io.reactivex.Observable
import io.reactivex.Single
import org.threeten.bp.LocalDate
import org.threeten.bp.Month

fun Single<AttendanceResponse?>.mapAttendanceList(start: LocalDate, end: LocalDate?, getTimes: () -> Single<List<Time>>): Single<List<Attendance>> {
    val endDate = end ?: start.plusDays(4)
    var excuseActive = false
    var sentExcuses = emptyList<SentExcuse>()
    return map {
        it.run {
            excuseActive = this.excuseActive
            sentExcuses = this.sentExcuses
            lessons
        }
    }.flatMapObservable { Observable.fromIterable(it) }.flatMap { a ->
        getTimes().flatMapObservable { times ->
            Observable.fromIterable(times.filter { time -> time.id == a.timeId })
        }.map {
            val sentExcuse = sentExcuses.firstOrNull { excuse -> excuse.date == a.date && excuse.timeId == a.timeId }
            a.apply {
                presence = a.categoryId == PRESENCE.id || a.categoryId == ABSENCE_FOR_SCHOOL_REASONS.id
                absence = a.categoryId == ABSENCE_UNEXCUSED.id || a.categoryId == ABSENCE_EXCUSED.id
                lateness = a.categoryId == EXCUSED_LATENESS.id || a.categoryId == UNEXCUSED_LATENESS.id
                excused = a.categoryId == ABSENCE_EXCUSED.id || a.categoryId == EXCUSED_LATENESS.id
                exemption = a.categoryId == EXEMPTION.id
                excusable = excuseActive && (absence || lateness) && !excused && sentExcuse == null
                name = (values().singleOrNull { category -> category.id == categoryId } ?: UNKNOWN).title
                number = it.number
                if (sentExcuse != null)
                    excuseStatus = SentExcuse.Status.getByValue(sentExcuse.status)
            }
        }
    }.filter {
        it.date.toLocalDate() >= start && it.date.toLocalDate() <= endDate
    }.toList().map { list -> list.sortedWith(compareBy({ it.date }, { it.number })) }
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
