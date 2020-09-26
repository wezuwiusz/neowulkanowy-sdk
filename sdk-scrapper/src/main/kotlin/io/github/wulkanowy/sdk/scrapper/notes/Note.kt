package io.github.wulkanowy.sdk.scrapper.notes

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.Date

@JsonClass(generateAdapter = true)
data class Note(

    @Json(name = "DataWpisu")
    val date: Date,

    @Json(name = "Nauczyciel")
    val teacher: String,

    @Json(name = "Kategoria")
    val category: String,

    @Json(name = "TrescUwagi")
    val content: String,

    @Json(name = "Punkty")
    val points: String = "",

    @Json(name = "PokazPunkty")
    val showPoints: Boolean = false,

    @Json(name = "KategoriaTyp")
    val categoryType: Int = 0
) {

    @Transient
    lateinit var teacherSymbol: String
}
