package io.github.wulkanowy.sdk.mapper

import io.github.wulkanowy.sdk.mobile.dictionaries.Teacher
import io.github.wulkanowy.sdk.pojo.Recipient
import io.github.wulkanowy.sdk.mobile.messages.Recipient as MobileRecipient
import io.github.wulkanowy.sdk.scrapper.messages.Recipient as ScrapperRecipient

fun List<ScrapperRecipient>.mapRecipients() = map {
    Recipient(
        name = it.name,
        mailboxGlobalKey = it.mailboxGlobalKey,
    )
}

fun List<Recipient>.mapFromRecipientsToScraper() = map {
    ScrapperRecipient(
        name = it.name,
        mailboxGlobalKey = it.mailboxGlobalKey,
    )
}

fun List<Recipient>.mapFromRecipientsToMobile() = map {
    MobileRecipient(
        loginId = it.mailboxGlobalKey.toInt(),
        name = it.name
    )
}

fun List<MobileRecipient>.mapFromMobileToRecipients() = map {
    Recipient(
        mailboxGlobalKey = it.loginId.toString(),
        name = it.name,
    )
}

fun List<Teacher>.mapRecipients(reportingUnitId: Int) = map {
    Recipient(
        name = "${it.name} ${it.surname}",
        mailboxGlobalKey = reportingUnitId.toString(),
    )
}
