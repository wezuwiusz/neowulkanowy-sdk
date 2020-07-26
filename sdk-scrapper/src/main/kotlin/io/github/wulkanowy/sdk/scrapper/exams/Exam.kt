package io.github.wulkanowy.sdk.scrapper.exams

import com.google.gson.annotations.SerializedName
import java.util.Date

data class Exam(

    @SerializedName("DataModyfikacji")
    val entryDate: Date,

    @SerializedName("DisplayValue")
    val subject: String,

    @SerializedName("Rodzaj")
    val type: String,

    @SerializedName("Opis")
    val description: String,

    @SerializedName("PracownikModyfikujacyDisplay")
    val teacher: String
) {

    lateinit var date: Date

    lateinit var group: String

    lateinit var teacherSymbol: String
}
