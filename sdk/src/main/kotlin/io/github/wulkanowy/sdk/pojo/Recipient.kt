package io.github.wulkanowy.sdk.pojo

data class Recipient(
    val mailboxGlobalKey: String,
    val fullName: String,
    val userName: String,
    val studentName: String,
    val schoolNameShort: String,
    val type: MailboxType,
)
