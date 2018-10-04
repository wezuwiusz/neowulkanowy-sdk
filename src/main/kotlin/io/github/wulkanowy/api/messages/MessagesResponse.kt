package io.github.wulkanowy.api.messages

data class MessagesResponse<out T>(

        val success: Boolean,

        val data: T?
)
