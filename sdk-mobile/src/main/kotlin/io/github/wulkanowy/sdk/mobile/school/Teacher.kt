package io.github.wulkanowy.sdk.mobile.school

import com.google.gson.annotations.SerializedName

data class Teacher(

    @SerializedName("IdPracownik")
    val employeeId: Int,

    @SerializedName("IdPrzedmiot")
    val subjectId: Int,

    @SerializedName("Rola")
    val role: String
)
