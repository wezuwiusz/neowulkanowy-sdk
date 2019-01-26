package io.github.wulkanowy.api.register

import io.github.wulkanowy.api.Api

data class Student(
        val email: String,
        val symbol: String,
        val studentId: Int,
        val studentName: String,
        val schoolSymbol: String,
        @Deprecated("use description val")
        val schoolName: String,
        val description: String,
        val loginType: Api.LoginType
)
