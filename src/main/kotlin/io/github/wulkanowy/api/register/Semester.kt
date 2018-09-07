package io.github.wulkanowy.api.register

data class Semester(
        val diaryId: String,
        val diaryName: String,
        val semesterId: Int,
        val semesterNumber: Int,
        val current: Boolean = false
)
