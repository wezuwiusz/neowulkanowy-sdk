package io.github.wulkanowy.sdk.mapper

import io.github.wulkanowy.sdk.mobile.dictionaries.Teacher
import io.github.wulkanowy.sdk.pojo.Recipient
import io.github.wulkanowy.sdk.mobile.messages.Recipient as MobileRecipient
import io.github.wulkanowy.sdk.scrapper.messages.Recipient as ScrapperRecipient

fun List<ScrapperRecipient>.mapRecipients() = map {
    it.mapToRecipient()
}

fun ScrapperRecipient.mapToRecipient() = Recipient(
    mailboxGlobalKey = mailboxGlobalKey,
    fullName = fullName,
    userName = userName,
    studentName = studentName,
    schoolNameShort = schoolNameShort,
)

fun List<Recipient>.mapFromRecipientsToMobile() = map {
    MobileRecipient(
        loginId = it.mailboxGlobalKey.toInt(),
        name = it.userName
    )
}

fun List<MobileRecipient>.mapFromMobileToRecipients() = map {
    Recipient(
        mailboxGlobalKey = it.loginId.toString(),
        fullName = it.name,
        userName = it.name,
        studentName = "",
        schoolNameShort = "",
    )
}

fun List<Teacher>.mapRecipients(reportingUnitId: Int) = map {
    Recipient(
        mailboxGlobalKey = reportingUnitId.toString(),
        fullName = "${it.name} ${it.surname}",
        userName = "${it.name} ${it.surname}",
        studentName = "",
        schoolNameShort = "",
    )
}
