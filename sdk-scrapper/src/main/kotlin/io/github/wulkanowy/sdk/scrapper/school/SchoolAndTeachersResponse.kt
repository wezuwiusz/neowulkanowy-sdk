package io.github.wulkanowy.sdk.scrapper.school

import com.google.gson.annotations.SerializedName

class SchoolAndTeachersResponse {

    @SerializedName("Nauczyciele")
    var teachers: List<Teacher> = emptyList()

    @SerializedName("Szkola")
    lateinit var school: School
}
