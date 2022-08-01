package io.github.wulkanowy.sdk.scrapper.school

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class SchoolAndTeachersResponse {

    @SerialName("Nauczyciele")
    var teachers: List<Teacher> = emptyList()

    @SerialName("Szkola")
    lateinit var school: School
}
