package io.github.wulkanowy.sdk.mapper

import io.github.wulkanowy.sdk.pojo.Mailbox
import io.github.wulkanowy.sdk.pojo.MailboxType
import io.github.wulkanowy.sdk.hebe.models.Mailbox as HebeMailbox
import io.github.wulkanowy.sdk.pojo.Mailbox as SdkMailbox
import io.github.wulkanowy.sdk.scrapper.messages.Mailbox as ScrapperMailbox

@JvmName("MapScrapperMailboxes")
internal fun List<ScrapperMailbox>.mapMailboxes(): List<Mailbox> = map {
    SdkMailbox(
        globalKey = it.globalKey,
        fullName = it.fullName,
        userName = it.userName,
        schoolNameShort = it.schoolNameShort,
        studentName = it.studentName,
        type = MailboxType.fromLetter(it.type.letter),
    )
}

@JvmName("MapHebeMailboxes")
internal fun List<HebeMailbox>.mapMailboxes(): List<Mailbox> = map {
    val mailboxInfo = it.name.split(" - ")

    SdkMailbox(
        globalKey = it.globalKey,
        fullName = mailboxInfo[0],
        userName = mailboxInfo[0],
        schoolNameShort = mailboxInfo[2],
        studentName = mailboxInfo[0],
        type = MailboxType.fromLetter(mailboxInfo[1]),
    )
}
