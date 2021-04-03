package io.github.wulkanowy.sdk.pojo

data class TimetableFull(
    val headers: List<TimetableDayHeader>,
    val lessons: List<Timetable>,
    val additional: List<TimetableAdditional>
)
