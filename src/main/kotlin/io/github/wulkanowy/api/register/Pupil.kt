package io.github.wulkanowy.api.register

import io.github.wulkanowy.api.Api

data class Pupil(
        val email: String,
        val symbol: String,
        val studentId: Int,
        val studentName: String,
        val schoolSymbol: String,
        val schoolName: String,
        val loginType: Api.LoginType
)
