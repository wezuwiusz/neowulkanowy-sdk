package io.github.wulkanowy.api

data class ApiResponse<out T>(

        val success: Boolean,

        val data: T?
)
