package io.github.wulkanowy.sdk.pojo

import io.github.wulkanowy.sdk.scrapper.attendance.SentExcuseStatus
import java.time.LocalDate

data class Attendance(
    val number: Int,
    val date: LocalDate,
    val timeId: Int,
    val subject: String,
    val name: String,
    val categoryId: Int,
    val presence: Boolean,
    val absence: Boolean,
    val exemption: Boolean,
    val lateness: Boolean,
    val excused: Boolean,
    val deleted: Boolean,
    val excusable: Boolean,
    val excuseStatus: SentExcuseStatus?,
)
