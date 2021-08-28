package io.github.wulkanowy.sdk.scrapper.exams

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.Date

@JsonClass(generateAdapter = true)
data class Exam(

    @Json(name = "DataModyfikacji")
    val entryDate: Date,

    @Json(name = "Nazwa")
    val subject: String,

    @Json(name = "Rodzaj")
    val type: String,

    @Json(name = "Opis")
    val description: String,

    @Json(name = "Pracownik")
    val teacher: String
) {

    @Transient
    lateinit var date: Date

    @Transient
    lateinit var teacherSymbol: String
}
