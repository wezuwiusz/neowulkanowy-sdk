package io.github.wulkanowy.sdk.scrapper.notes

import com.google.gson.annotations.SerializedName
import java.util.Date

class Note {

    @SerializedName("DataWpisu")
    lateinit var date: Date

    @SerializedName("Nauczyciel")
    lateinit var teacher: String

    lateinit var teacherSymbol: String

    @SerializedName("Kategoria")
    lateinit var category: String

    @SerializedName("TrescUwagi")
    lateinit var content: String

    @SerializedName("Punkty")
    var points: String = ""

    @SerializedName("PokazPunkty")
    var showPoints = false

    @SerializedName("KategoriaTyp")
    var categoryType: Int = 0

    enum class CategoryType(val id: Int) {
        UNKNOWN(0),
        POSITIVE(1),
        NEUTRAL(2),
        NEGATIVE(3);

        companion object {
            fun getByValue(value: Int) = values().singleOrNull { it.id == value } ?: UNKNOWN
        }
    }
}
