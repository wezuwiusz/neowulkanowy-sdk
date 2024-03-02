package io.github.wulkanowy.sdk.mapper

import io.github.wulkanowy.sdk.pojo.DirectorInformation
import io.github.wulkanowy.sdk.pojo.LastAnnouncement
import io.github.wulkanowy.sdk.scrapper.home.DirectorInformation as ScrapperDirectorInformation
import io.github.wulkanowy.sdk.scrapper.home.LastAnnouncement as ScrapperLastAnnouncement

internal fun List<ScrapperDirectorInformation>.mapDirectorInformation() = map {
    DirectorInformation(
        date = it.date,
        subject = it.subject,
        content = it.content,
    )
}

internal fun List<ScrapperLastAnnouncement>.mapLastAnnouncements() = map {
    LastAnnouncement(
        author = it.author,
        date = it.date,
        subject = it.subject,
        content = it.content,
    )
}
