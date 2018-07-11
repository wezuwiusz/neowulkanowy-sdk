package io.github.wulkanowy.sdk.attendance

import com.google.gson.annotations.SerializedName

data class Attendance(

        @SerializedName("IdKategoria")
        val id: Int,

        @SerializedName("Numer")
        val number: Int,

        @SerializedName("IdPoraLekcji")
        val lessonTimeId: Int,

        @SerializedName("Dzien")
        val date: Long,

        @SerializedName("DzienTekst")
        val dateText: String,

        @SerializedName("IdPrzedmiot")
        val subjectId: Int,

        @SerializedName("PrzedmiotNazwa")
        val subjectName: String
)
