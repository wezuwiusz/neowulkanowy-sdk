package io.github.wulkanowy.sdk.scrapper.attendance

import org.threeten.bp.Month

data class AttendanceSummary(
    val month: Month,
    val presence: Int,
    val absence: Int,
    val absenceExcused: Int,
    val absenceForSchoolReasons: Int,
    val lateness: Int,
    val latenessExcused: Int,
    val exemption: Int
)
