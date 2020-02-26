package io.github.wulkanowy.sdk.mapper

import io.github.wulkanowy.sdk.pojo.LuckyNumber
import io.github.wulkanowy.sdk.scrapper.home.LuckyNumber as ScrapperLuckyNumber

fun List<ScrapperLuckyNumber>.mapLuckyNumbers(): List<LuckyNumber> {
    return map {
        LuckyNumber(
            institution = it.institution,
            school = it.school,
            number = it.number
        )
    }
}
