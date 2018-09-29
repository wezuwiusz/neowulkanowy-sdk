package io.github.wulkanowy.api.attendance

data class AttendanceSummary(
        val month: String,
        val presence: Int,
        val absence: Int,
        val absenceExcused: Int,
        val absenceForSchoolReasons: Int,
        val lateness: Int,
        val latenessExcused: Int,
        val exemption: Int
)
