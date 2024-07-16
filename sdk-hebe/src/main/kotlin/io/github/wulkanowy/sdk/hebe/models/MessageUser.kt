package io.github.wulkanowy.sdk.hebe.models

data class MessageUser(
    val partition: String,
    val globalKey: String,
    val name: String,
)
