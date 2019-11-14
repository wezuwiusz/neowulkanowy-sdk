package io.github.wulkanowy.sdk.exams

import com.google.gson.annotations.SerializedName

data class Exam(

    @SerializedName("Id")
    val id: Int,

    @SerializedName("IdPrzedmiot")
    val subjectId: Int,

    @SerializedName("IdPracownik")
    val employeeId: Int,

    @SerializedName("IdOddzial")
    val classId: Int?,

    @SerializedName("IdPodzial")
    val divideId: Int?,

    @SerializedName("PodzialNazwa")
    val divideName: String?,

    @SerializedName("Rodzaj")
    val type: Boolean, // false - quiz, true - test

    @SerializedName("Opis")
    val description: String,

    @SerializedName("Data")
    val date: Long,

    @SerializedName("DataTekst")
    val dateText: String
)
