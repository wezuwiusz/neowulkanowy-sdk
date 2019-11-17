package io.github.wulkanowy.sdk.mobile.school

import com.google.gson.annotations.SerializedName

data class TeachersResponse(

    @SerializedName("NauczycieleSzkola")
    val schoolTeachers: List<Teacher>,

    @SerializedName("NauczycielePrzedmioty")
    val teachersSubjects: List<Teacher>
)
