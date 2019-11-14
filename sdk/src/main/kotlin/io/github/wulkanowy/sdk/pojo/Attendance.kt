package io.github.wulkanowy.sdk.pojo

import org.threeten.bp.LocalDate

data class Attendance(
    val number: Int,
    val date: LocalDate,
    val subject: String,
    val name: String,
    val categoryId: Int,
    val presence: Boolean,
    val absence: Boolean,
    val exemption: Boolean,
    val lateness: Boolean,
    val excused: Boolean,
    val deleted: Boolean,
    val excusable: Boolean
)
