package io.github.wulkanowy.sdk.mapper

import io.github.wulkanowy.sdk.pojo.Conference
import io.github.wulkanowy.sdk.scrapper.conferences.Conference as ScrapperConference

fun List<ScrapperConference>.mapConferences() = map {
    Conference(
        title = it.title,
        subject = it.subject,
        agenda = it.agenda,
        presentOnConference = it.presentOnConference,
        online = it.online,
        date = it.date,
        id = it.id
    )
}
