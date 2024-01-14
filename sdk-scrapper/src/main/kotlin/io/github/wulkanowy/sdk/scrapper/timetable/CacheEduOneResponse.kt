package io.github.wulkanowy.sdk.scrapper.timetable

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class CacheEduOneResponse(
    @SerialName("isPokazLekcjeZrealizowaneOn")
    val showCompletedLessons: Boolean,
)
