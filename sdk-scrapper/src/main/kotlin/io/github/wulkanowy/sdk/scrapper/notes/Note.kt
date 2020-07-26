package io.github.wulkanowy.sdk.scrapper.notes

import com.google.gson.annotations.SerializedName
import java.util.Date

data class Note(

    @SerializedName("DataWpisu")
    val date: Date,

    @SerializedName("Nauczyciel")
    val teacher: String,

    @SerializedName("Kategoria")
    val category: String,

    @SerializedName("TrescUwagi")
    val content: String,

    @SerializedName("Punkty")
    val points: String = "",

    @SerializedName("PokazPunkty")
    val showPoints: Boolean = false,

    @SerializedName("KategoriaTyp")
    val categoryType: Int = 0
) {

    lateinit var teacherSymbol: String
}
