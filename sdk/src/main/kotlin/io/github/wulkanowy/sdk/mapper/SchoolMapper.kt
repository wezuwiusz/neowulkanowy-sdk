package io.github.wulkanowy.sdk.mapper

import io.github.wulkanowy.sdk.pojo.School
import io.github.wulkanowy.sdk.scrapper.school.School as ScrapperSchool

fun ScrapperSchool.mapSchool() = School(
    name = name,
    address = address,
    contact = contact,
    headmaster = headmaster,
    pedagogue = pedagogue,
)
