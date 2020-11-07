package io.github.wulkanowy.sdk.mapper

import io.github.wulkanowy.sdk.pojo.DirectorInformation
import io.github.wulkanowy.sdk.scrapper.home.DirectorInformation as ScrapperDirectorInformation

fun List<ScrapperDirectorInformation>.mapDirectorInformation() = map {
    DirectorInformation(
        date = it.date,
        subject = it.subject,
        content = it.content
    )
}
