package io.github.wulkanowy.sdk.scrapper.school

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class Teacher(

    @SerialName("Nauczyciel")
    val name: String,

    @SerialName("Nazwa")
    val subject: String,
) {

    @Transient
    var short: String = ""
}
