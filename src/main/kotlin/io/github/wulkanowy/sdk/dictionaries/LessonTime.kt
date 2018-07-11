package io.github.wulkanowy.sdk.dictionaries

import com.google.gson.annotations.SerializedName

data class LessonTime(

        @SerializedName("Id")
        val id: Int,

        @SerializedName("Numer")
        val number: Int,

        @SerializedName("Poczatek")
        val start: Int,

        @SerializedName("PoczatekTekst")
        val startText: String,

        @SerializedName("Koniec")
        val end: Int,

        @SerializedName("KoniecTekst")
        val endText: String
)
