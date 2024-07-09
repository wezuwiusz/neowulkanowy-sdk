package io.github.wulkanowy.sdk.mapper

import io.github.wulkanowy.sdk.pojo.Conference
import io.github.wulkanowy.sdk.toLocalDateTime
import java.time.ZoneId
import io.github.wulkanowy.sdk.hebe.models.Meeting as HebeConference
import io.github.wulkanowy.sdk.scrapper.conferences.Conference as ScrapperConference

@JvmName("ScrapperConferenceMapper")
internal fun List<ScrapperConference>.mapConferences(zoneId: ZoneId) = map {
    Conference(
        place = it.place,
        topic = it.topic,
        agenda = it.agenda,
        presentOnConference = it.presentOnConference,
        online = it.online,
        date = it.date.atZone(zoneId),
        id = it.id,
    )
}

@JvmName("HebeConferenceMapper")
internal fun List<HebeConference>.mapConferences(zoneId: ZoneId) = map {
    Conference(
        place = it.where,
        topic = it.why,
        agenda = it.agenda,
        presentOnConference = "",
        online = it.online,
        date = it.`when`.timestamp
            .toLocalDateTime()
            .atZone(zoneId),
        id = it.id,
    )
}
