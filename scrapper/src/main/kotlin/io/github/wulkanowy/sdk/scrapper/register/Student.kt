package io.github.wulkanowy.sdk.scrapper.register

import io.github.wulkanowy.sdk.scrapper.Scrapper

data class Student(
    val email: String,
    val symbol: String,
    val studentId: Int,
    val studentName: String,
    val schoolSymbol: String,
    val schoolName: String,
    val className: String,
    val classId: Int,
    val loginType: Scrapper.LoginType
)
