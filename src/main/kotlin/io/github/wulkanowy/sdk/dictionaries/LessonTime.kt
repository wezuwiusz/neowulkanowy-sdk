package io.github.wulkanowy.sdk.dictionaries

import com.google.gson.annotations.SerializedName

data class LessonTime(

    @SerializedName("Id")
    var id: Int,

    @SerializedName("Numer")
    var number: Int,

    @SerializedName("Poczatek")
    var start: Int,

    @SerializedName("PoczatekTekst")
    var startText: String,

    @SerializedName("Koniec")
    var end: Int,

    @SerializedName("KoniecTekst")
    var endText: String
)
