package io.github.wulkanowy.sdk.mobile.homework

import com.google.gson.annotations.SerializedName

data class Homework(

    @SerializedName("Id")
    val id: Int,

    @SerializedName("IdUczen")
    val studentId: Int,

    @SerializedName("Data")
    val date: Long,

    @SerializedName("DataTekst")
    val dateText: String,

    @SerializedName("IdPracownik")
    val employeeId: Int,

    @SerializedName("IdPrzedmiot")
    val subjectId: Int,

    @SerializedName("Opis")
    val content: String
)
