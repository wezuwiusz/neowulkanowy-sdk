package io.github.wulkanowy.api.school

import com.google.gson.annotations.SerializedName

data class Teacher(

    @SerializedName("Nauczyciel")
    val name: String,

    val short: String,

    @SerializedName("Nazwa")
    val subject: String
)
