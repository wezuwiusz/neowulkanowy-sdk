package io.github.wulkanowy.sdk.pojo

data class Attachment(
    val id: Int,
    val messageId: Int,
    val oneDriveId: String,
    val url: String,
    val filename: String
)
