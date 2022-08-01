package io.github.wulkanowy.sdk.scrapper.attendance

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Subject(

    @SerialName("Nazwa")
    var name: String = "Wszystkie",

    @SerialName("Id")
    var value: Int = -1
)
