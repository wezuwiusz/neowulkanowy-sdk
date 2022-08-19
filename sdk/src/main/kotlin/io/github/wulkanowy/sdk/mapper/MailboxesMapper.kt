package io.github.wulkanowy.sdk.mapper

import io.github.wulkanowy.sdk.pojo.Mailbox
import io.github.wulkanowy.sdk.pojo.MailboxType
import io.github.wulkanowy.sdk.pojo.Mailbox as SdkMailbox
import io.github.wulkanowy.sdk.scrapper.messages.Mailbox as ScrapperMailbox

fun List<ScrapperMailbox>.mapMailboxes(): List<Mailbox> {
    return map {
        SdkMailbox(
            globalKey = it.globalKey,
            fullName = it.fullName,
            userName = it.userName,
            schoolNameShort = it.schoolNameShort,
            studentName = it.studentName,
            type = MailboxType.fromLetter(it.type.letter),
        )
    }
}
