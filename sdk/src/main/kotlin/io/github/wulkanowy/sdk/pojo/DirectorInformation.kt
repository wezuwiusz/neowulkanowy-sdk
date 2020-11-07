package io.github.wulkanowy.sdk.pojo

import java.time.LocalDate

data class DirectorInformation(
    val date: LocalDate,
    val subject: String,
    val content: String
)
