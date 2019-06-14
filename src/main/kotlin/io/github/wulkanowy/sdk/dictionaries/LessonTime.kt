package io.github.wulkanowy.sdk.dictionaries

import com.google.gson.annotations.SerializedName

data class LessonTime(

        @SerializedName("Id")
        val id: Int,

        @SerializedName("Numer")
        val number: Int,

        @SerializedName("Poczatek")
        val start: Long,

        @SerializedName("PoczatekTekst")
        val startText: String,

        @SerializedName("Koniec")
        val end: Long,

        @SerializedName("KoniecTekst")
        val endText: String
)
