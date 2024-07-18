package io.github.wulkanowy.sdk.mapper

import io.github.wulkanowy.sdk.extractNameFromRecipient
import io.github.wulkanowy.sdk.extractSchoolShortFromRecipient
import io.github.wulkanowy.sdk.extractTypeFromRecipient
import io.github.wulkanowy.sdk.pojo.MailboxType
import io.github.wulkanowy.sdk.pojo.Recipient
import io.github.wulkanowy.sdk.hebe.models.Recipient as HebeRecipient
import io.github.wulkanowy.sdk.scrapper.messages.Recipient as ScrapperRecipient

@JvmName("ScrapperMapRecipient")
internal fun List<ScrapperRecipient>.mapRecipients() = map {
    it.mapToRecipient()
}

@JvmName("ScrapperMapToRecipient")
internal fun ScrapperRecipient.mapToRecipient() = Recipient(
    mailboxGlobalKey = mailboxGlobalKey,
    fullName = fullName,
    userName = userName,
    studentName = studentName,
    schoolNameShort = schoolNameShort,
    type = MailboxType.fromLetter(type.letter),
)

@JvmName("HebeMapRecipient")
internal fun List<HebeRecipient>.mapRecipients() = map {
    it.mapToRecipient()
}

@JvmName("HebeMapToRecipient")
internal fun HebeRecipient.mapToRecipient() = Recipient(
    mailboxGlobalKey = globalKey,
    fullName = name,
    userName = name,
    studentName = name.extractNameFromRecipient(),
    schoolNameShort = name.extractSchoolShortFromRecipient(),
    type = name.extractTypeFromRecipient(),
)
