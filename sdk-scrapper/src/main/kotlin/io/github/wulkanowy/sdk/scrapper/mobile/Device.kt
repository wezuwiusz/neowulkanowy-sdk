package io.github.wulkanowy.sdk.scrapper.mobile

import io.github.wulkanowy.sdk.scrapper.adapter.CustomDateAdapter
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class Device(

    @SerialName("Id")
    val id: Int = 0,

    @SerialName("IdentyfikatorUrzadzenia")
    val deviceId: String? = null,

    @SerialName("NazwaUrzadzenia")
    val name: String? = null,

    @SerialName("DataUtworzenia")
    @Serializable(with = CustomDateAdapter::class)
    val createDate: LocalDateTime? = null,

    @SerialName("DataModyfikacji")
    @Serializable(with = CustomDateAdapter::class)
    val modificationDate: LocalDateTime? = null,
)
