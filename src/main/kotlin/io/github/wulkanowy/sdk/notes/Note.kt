package io.github.wulkanowy.sdk.notes

import com.google.gson.annotations.SerializedName

data class Note(

        @SerializedName("Id")
        val id: Int,

        @SerializedName("IdKategoriaUwag")
        val noteCategoryId: Int,

        @SerializedName("IdUczen")
        val studentId: Int,

        @SerializedName("UczenImie")
        val studentName: String,

        @SerializedName("UczenNazwisko")
        val studentSurname: String,

        @SerializedName("IdPracownik")
        val employeeId: Int,

        @SerializedName("PracownikImie")
        val employeeName: String,

        @SerializedName("PracownikNazwisko")
        val employeeSurname: String,

        @SerializedName("DataWpisu")
        val entryDate: Long,

        @SerializedName("DataWpisuTekst")
        val entryDateText: String,

        @SerializedName("DataModyfikacji")
        val modificationDate: Long?,

        @SerializedName("DataModyfikacjiTekst")
        val modificationDateText: String?,

        @SerializedName("UwagaKey")
        val noteKey: String,

        @SerializedName("TrescUwagi")
        val content: String
)
