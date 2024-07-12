package io.github.wulkanowy.sdk.mapper

import io.github.wulkanowy.sdk.pojo.LuckyNumber
import io.github.wulkanowy.sdk.hebe.models.LuckyNumber as HebeLuckyNumber
import io.github.wulkanowy.sdk.scrapper.home.LuckyNumber as ScrapperLuckyNumber

@JvmName("mapScrapperLuckyNumber")
internal fun List<ScrapperLuckyNumber>.mapLuckyNumbers() = map {
    LuckyNumber(
        unitName = it.unitName,
        school = it.school,
        number = it.number,
    )
}

@JvmName("mapHebeLuckyNumber")
internal fun HebeLuckyNumber.mapLuckyNumbers() = listOf(
    LuckyNumber(
        unitName = "",
        school = "",
        number = number,
    ),
)
