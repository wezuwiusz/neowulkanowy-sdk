package io.github.wulkanowy.sdk.mapper

import io.github.wulkanowy.sdk.mobile.dictionaries.Teacher
import io.github.wulkanowy.sdk.scrapper.messages.Recipient as ScrapperRecipient
import io.github.wulkanowy.sdk.mobile.messages.Recipient as MobileRecipient
import io.github.wulkanowy.sdk.pojo.Recipient

fun List<ScrapperRecipient>.mapRecipients(): List<Recipient> {
    return map {
        Recipient(
            id = it.id,
            hash = it.hash,
            loginId = it.loginId,
            name = it.name,
            reportingUnitId = it.reportingUnitId,
            role = it.role,
            shortName = it.shortName.orEmpty()
        )
    }
}

fun List<Recipient>.mapFromRecipientsToScraper(): List<ScrapperRecipient> {
    return map {
        ScrapperRecipient(
            id = it.id,
            hash = it.hash,
            loginId = it.loginId,
            name = it.name,
            reportingUnitId = it.reportingUnitId,
            role = it.role,
            shortName = it.shortName
        )
    }
}

fun List<Recipient>.mapFromRecipientsToMobile(): List<MobileRecipient> {
    return map {
        MobileRecipient(
            // id = it.id,
            // hash = it.hash,
            loginId = it.loginId,
            name = it.name
            // reportingUnitId = it.reportingUnitId,
            // role = it.role,
            // shortName = it.shortName
        )
    }
}

fun List<MobileRecipient>.mapFromMobileToRecipients(): List<Recipient> {
    return map {
        Recipient(
            id = "",
            loginId = it.loginId,
            hash = "",
            name = it.name,
            reportingUnitId = 0,
            role = 0,
            shortName = ""
        )
    }
}

fun List<Teacher>.mapRecipients(reportingUnitId: Int): List<Recipient> {
    return map {
        Recipient(
            id = it.loginId.toString(),
            shortName = it.code,
            role = 2,
            reportingUnitId = reportingUnitId,
            name = "${it.name} ${it.surname}",
            loginId = it.loginId,
            hash = "NIE UŻYWAJ NADAWCÓW POBRANYCH W TRYBIE API DO WYSYŁANIA WIADOMOŚCI W TRYBIE SCRAPPER ANI ODWROTNIE" // TODO: throw exception then
        )
    }
}
