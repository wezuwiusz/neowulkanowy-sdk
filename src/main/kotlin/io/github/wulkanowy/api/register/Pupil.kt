package io.github.wulkanowy.api.register

data class Pupil(
        val email: String,
        val symbol: String,
        val studentId: String,
        val studentName: String,
        val schoolId: String,
        val schoolName: String,
        val diaryId: String,
        val diaryName: String,
        val semesterId: String,
        val semesterNumber: String
)
