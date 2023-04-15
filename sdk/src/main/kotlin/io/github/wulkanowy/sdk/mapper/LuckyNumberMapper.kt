package io.github.wulkanowy.sdk.mapper

import io.github.wulkanowy.sdk.pojo.LuckyNumber
import io.github.wulkanowy.sdk.scrapper.home.LuckyNumber as ScrapperLuckyNumber

internal fun List<ScrapperLuckyNumber>.mapLuckyNumbers() = map {
    LuckyNumber(
        unitName = it.unitName,
        school = it.school,
        number = it.number,
    )
}
