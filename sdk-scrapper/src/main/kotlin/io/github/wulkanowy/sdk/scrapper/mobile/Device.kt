package io.github.wulkanowy.sdk.scrapper.mobile

import io.github.wulkanowy.sdk.scrapper.adapter.CustomDateAdapter
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames
import java.time.LocalDateTime

@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class Device(

    @SerialName("Id")
    @JsonNames("id")
    val id: Int = 0,

    @SerialName("IdentyfikatorUrzadzenia")
    val deviceId: String? = null,

    @SerialName("NazwaUrzadzenia")
    @JsonNames("nazwa")
    val name: String? = null,

    @SerialName("DataUtworzenia")
    @JsonNames("dataCertyfikatu")
    @Serializable(with = CustomDateAdapter::class)
    val createDate: LocalDateTime? = null,

    @SerialName("DataModyfikacji")
    @Serializable(with = CustomDateAdapter::class)
    val modificationDate: LocalDateTime? = null,
)
